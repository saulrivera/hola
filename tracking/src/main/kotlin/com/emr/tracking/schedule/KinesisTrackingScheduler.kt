package com.emr.tracking.schedule

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.TracingManager
import com.emr.tracking.model.KontaktTelemetryResponse
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.common.KinesisClientUtil
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.exceptions.InvalidStateException
import software.amazon.kinesis.exceptions.ShutdownException
import software.amazon.kinesis.lifecycle.events.*
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory
import software.amazon.kinesis.retrieval.polling.PollingConfig
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ExecutionException
import org.apache.commons.lang3.RandomUtils
import software.amazon.awssdk.core.SdkBytes
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest
import software.amazon.kinesis.common.InitialPositionInStream
import software.amazon.kinesis.common.InitialPositionInStreamExtended
import kotlin.math.log

@Component
class KinesisTrackingScheduler(
    private val appProperties: AppProperties,
    private val tracingManager: TracingManager
) {
    var streamName: String? = null
    var kinesisClient: KinesisAsyncClient? = null

    @Bean
    fun telemetryDataTracking() {
        val region = Region.of(appProperties.awsRegion)
        streamName = appProperties.awsStreamName
        kinesisClient = KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder().region(region))

        val dynamoDbClient = DynamoDbAsyncClient.builder().region(region).build()
        val cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build()
        val configsBuilder = ConfigsBuilder(
            streamName!!,
            streamName!!,
            kinesisClient!!,
            dynamoDbClient,
            cloudWatchClient,
            UUID.randomUUID().toString(),
            KinesisManagerFactory(tracingManager)
        )

        val pollingConfig = PollingConfig(appProperties.awsStreamName, kinesisClient)

        val scheduler = Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig()
                .retrievalSpecificConfig(pollingConfig)
        )

        val schedulerThread = Thread(scheduler)
        schedulerThread.isDaemon = true
        schedulerThread.start()
    }
}

class KinesisManagerFactory(
    private val tracingManager: TracingManager
) : ShardRecordProcessorFactory {
    override fun shardRecordProcessor(): ShardRecordProcessor {
        return KinesisManager(tracingManager)
    }
}

class KinesisManager(
    private val tracingManager: TracingManager
) : ShardRecordProcessor {

    companion object {
        const val SHARD_ID_MDC_KEY = "ShardId"
        var shardId = ""
        val logger: Logger = LoggerFactory.getLogger(KinesisManager::class.java)
    }

    override fun initialize(initializationInput: InitializationInput?) {
        if (initializationInput != null) {
            shardId = initializationInput.shardId()
            MDC.put(SHARD_ID_MDC_KEY, shardId)
            try {
                logger.info("Initializing @ sequence: ${initializationInput.extendedSequenceNumber()}")
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY)
            }
        }
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {
        if (processRecordsInput != null) {
            MDC.put(SHARD_ID_MDC_KEY, shardId)
            try {
                logger.info("Processing ${processRecordsInput.records().size}")
                processRecordsInput.records()?.forEach {
                    val originalData = StandardCharsets.UTF_8.decode(it.data())
                    val response = Gson().fromJson(originalData.toString(), KontaktTelemetryResponse::class.java)
                    logger.info("Processing response: $originalData, with object: $response")
                    tracingManager.processBeaconStream(response)
                }
            } catch (e: Throwable) {
                logger.error("Caught throwable while processing records. Aborting.")
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY)
            }
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput?) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            logger.info("Lost lease, so terminating $leaseLostInput")
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput?) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            logger.info("Reached shard end checkpointing.")
            shardEndedInput?.checkpointer()?.checkpoint()
        } catch (e: ShutdownException) {
            logger.error("Exception while checkpointing at shard end. Giving up. $e")
        } catch (e: InvalidStateException) {
            logger.error("Exception while checkpointing at shard end. Giving up. $e")
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput?) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            logger.info("Scheduler is shutting down, checkpointing.")
            shutdownRequestedInput?.checkpointer()?.checkpoint()
        } catch (e: ShutdownException) {
            logger.error("Exception while checkpointing at requested shutdown. Giving up. $e")
        } catch (e: InvalidStateException) {
            logger.error("Exception while checkpointing at requested shutdown. Giving up. $e")
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }
}