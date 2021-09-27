package outland.emr.tracking.schedulers;

import outland.emr.tracking.config.KinesisConfProperties;
import outland.emr.tracking.managers.KinesisManager;
import outland.emr.tracking.managers.KinesisManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import java.util.UUID;

@Component
public class KinesisTracingScheduler {
    @Autowired
    private final KinesisManager kinesisManager;
    @Autowired
    private final KinesisConfProperties kinesisConfProperties;

    public KinesisTracingScheduler(KinesisManager kinesisManager, KinesisConfProperties kinesisConfProperties) {
        this.kinesisManager = kinesisManager;
        this.kinesisConfProperties = kinesisConfProperties;
    }

    @Bean
    public void telemetryDataTracing() {
        Region region = Region.of(kinesisConfProperties.getAwsRegion());
        String streamName = kinesisConfProperties.getStreamName();
        KinesisAsyncClient kinesisClient = KinesisClientUtil.
                createKinesisAsyncClient(KinesisAsyncClient.builder().region(region));

        DynamoDbAsyncClient dynamoDbClient = DynamoDbAsyncClient.builder().region(region).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();
        ConfigsBuilder configsBuilder = new ConfigsBuilder(
                streamName,
                streamName,
                kinesisClient,
                dynamoDbClient,
                cloudWatchClient,
                UUID.randomUUID().toString(),
                new KinesisManagerFactory(kinesisManager)
        );

        PollingConfig pollingConfig = new PollingConfig(streamName, kinesisClient);

        Scheduler scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig()
                .retrievalSpecificConfig(pollingConfig)
                .initialPositionInStreamExtended(
                        InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)
                )
        );

        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
}
