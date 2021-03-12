package com.emr.tracking.schedule

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.TracingManager
import com.emr.tracking.model.KontaktTelemetryResponse
import com.google.gson.Gson
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
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest

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
        pollingConfig.recordsFetcherFactory().idleMillisBetweenCalls(appProperties.appTracingFrequency.toLong())
        pollingConfig.idleTimeBetweenReadsInMillis(appProperties.appTracingFrequency.toLong())

        val scheduler = Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig().retrievalSpecificConfig(pollingConfig)
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

    override fun initialize(initializationInput: InitializationInput?) {
        println("Initialization completed")
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {
        processRecordsInput?.records()?.forEach {
            try {
                val originalData = StandardCharsets.UTF_8.decode(it.data())
                val response = Gson().fromJson(originalData.toString(), KontaktTelemetryResponse::class.java)
                runBlocking {
                    tracingManager.processBeaconStream(response)
                }
            } catch (e: Exception) {
                println("Error parsing record $e")
            }
        }

        try {
            processRecordsInput?.checkpointer()?.checkpoint()
        } catch (e: Exception) {
            println("Error during processing of records : $e")
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput?) {
        println("LeaseLostInput $leaseLostInput")
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput?) {
        try {
            shardEndedInput?.checkpointer()?.checkpoint()
        } catch (e: ShutdownException) {
            e.printStackTrace()
        } catch (e: InvalidStateException) {
            e.printStackTrace()
        }
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput?) {
        try {
            shutdownRequestedInput?.checkpointer()?.checkpoint()
        } catch (e: ShutdownException) {
            e.printStackTrace()
        } catch (e: InvalidStateException) {
            e.printStackTrace()
        }
    }
}