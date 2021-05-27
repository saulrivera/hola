package com.emr.tracing.logic;

import com.emr.tracing.models.mongo.Asset;
import com.emr.tracing.models.mongo.AssetBeacon;
import com.emr.tracing.models.mongo.Beacon;
import com.emr.tracing.repositories.mongo.MongoAssetBeaconRepository;
import com.emr.tracing.repositories.mongo.MongoAssetRepository;
import com.emr.tracing.repositories.redis.RedisAssetBeaconRepository;
import com.emr.tracing.repositories.redis.RedisAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetLogic {
    @Autowired
    private final MongoAssetRepository mongoAssetRepository;
    @Autowired
    private final MongoAssetBeaconRepository mongoAssetBeaconRepository;
    @Autowired
    private final RedisAssetRepository redisAssetRepository;
    @Autowired
    private final RedisAssetBeaconRepository redisAssetBeaconRepository;

    public AssetLogic(
            MongoAssetRepository mongoAssetRepository,
            MongoAssetBeaconRepository mongoAssetBeaconRepository,
            RedisAssetRepository redisAssetRepository,
            RedisAssetBeaconRepository redisAssetBeaconRepository
    ) {
        this.mongoAssetRepository = mongoAssetRepository;
        this.mongoAssetBeaconRepository = mongoAssetBeaconRepository;
        this.redisAssetRepository = redisAssetRepository;
        this.redisAssetBeaconRepository = redisAssetBeaconRepository;
    }

    public boolean isTableEmpty() {
        return !((long) mongoAssetRepository.findAll().size() > 0);
    }

    public void add(Asset asset) {
        this.mongoAssetRepository.save(asset);
        this.redisAssetRepository.add(createRedis(asset));
    }

    public void associate(Asset asset, String beaconMac) {
        AssetBeacon assetBeacon = new AssetBeacon(asset.getId(), beaconMac);
        this.mongoAssetBeaconRepository.save(assetBeacon);
        this.redisAssetBeaconRepository.add(createRedis(assetBeacon));
    }

    public com.emr.tracing.models.redis.Asset createRedis(Asset asset) {
        return new com.emr.tracing.models.redis.Asset(asset.getId(), asset.getLabel(), asset.getKind());
    }

    public com.emr.tracing.models.redis.AssetBeacon createRedis(AssetBeacon assetBeacon) {
        return new com.emr.tracing.models.redis.AssetBeacon(assetBeacon.getAssetId(), assetBeacon.getBeaconId());
    }

    public void syncWithRedis() {
        this.mongoAssetRepository.findAll().stream().map(this::createRedis).forEach(this.redisAssetRepository::add);
        this.mongoAssetBeaconRepository.findAll().stream().map(this::createRedis).forEach(this.redisAssetBeaconRepository::add);
    }
}
