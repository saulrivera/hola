package com.emr.tracing.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public class KinesisManagerFactory implements ShardRecordProcessorFactory {
    @Autowired
    private final KinesisManager kinesisManager;

    public KinesisManagerFactory(KinesisManager kinesisManager) {
        this.kinesisManager = kinesisManager;
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return kinesisManager;
    }
}
