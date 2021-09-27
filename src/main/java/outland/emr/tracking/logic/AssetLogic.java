package outland.emr.tracking.logic;

import outland.emr.tracking.models.mongo.Asset;
import outland.emr.tracking.models.mongo.AssetBeacon;
import outland.emr.tracking.repositories.mongo.MongoAssetBeaconRepository;
import outland.emr.tracking.repositories.mongo.MongoAssetRepository;
import outland.emr.tracking.repositories.redis.RedisAssetBeaconRepository;
import outland.emr.tracking.repositories.redis.RedisAssetRepository;
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

    public outland.emr.tracking.models.redis.Asset createRedis(Asset asset) {
        return new outland.emr.tracking.models.redis.Asset(asset.getId(), asset.getLabel(), asset.getKind());
    }

    public outland.emr.tracking.models.redis.AssetBeacon createRedis(AssetBeacon assetBeacon) {
        return new outland.emr.tracking.models.redis.AssetBeacon(assetBeacon.getAssetId(), assetBeacon.getBeaconId());
    }

    public void syncWithRedis() {
        this.mongoAssetRepository.findAll().stream().map(this::createRedis).forEach(this.redisAssetRepository::add);
        this.mongoAssetBeaconRepository.findAll().stream().map(this::createRedis).forEach(this.redisAssetBeaconRepository::add);
    }

    public void flushTable() {

    }
}
