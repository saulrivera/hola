package outland.emr.tracking.managers;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import outland.emr.tracking.models.csv.Siblings;
import outland.emr.tracking.models.mongo.Asset;
import outland.emr.tracking.models.mongo.Beacon;
import outland.emr.tracking.models.mongo.Staff;
import outland.emr.tracking.models.neo4j.Gateway;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DataTranslation {
    private final List<outland.emr.tracking.models.csv.Beacon> beacons;
    private final List<outland.emr.tracking.models.csv.Gateway> gateways;
    private final List<outland.emr.tracking.models.csv.Asset> assets;
    private final List<outland.emr.tracking.models.csv.Staff> staff;
    private final List<Siblings> siblings;

    public DataTranslation() {
        beacons = new CsvToBeanBuilder(getBeaconFile())
                .withType(outland.emr.tracking.models.csv.Beacon.class)
                .build()
                .parse();
        gateways = new CsvToBeanBuilder(getGatewaysFile())
                .withType(outland.emr.tracking.models.csv.Gateway.class)
                .build()
                .parse();
        assets = new CsvToBeanBuilder(getAssetsFile())
                .withType(outland.emr.tracking.models.csv.Asset.class)
                .build()
                .parse();
        staff = new CsvToBeanBuilder(getStaffFile())
                .withType(outland.emr.tracking.models.csv.Staff.class)
                .build()
                .parse();
        siblings = new CsvToBeanBuilder(getSiblingsFile())
                .withSeparator('\t')
                .withType(Siblings.class)
                .build()
                .parse();
    }

    public List<Beacon> loadBeacons() {
        return beacons
                .stream()
                .map(beacon -> new Beacon(
                        beacon.getId(),
                        beacon.getMac(),
                        beacon.getLabel(),
                        beacon.getType())
                )
                .collect(Collectors.toList());
    }

    public List<Gateway> loadGateways() {
        return gateways
                .stream()
                .map(gateway -> new Gateway(
                        gateway.getId(),
                        gateway.getLabel(),
                        gateway.getMac(),
                        gateway.getFloor(),
                        gateway.getPositionX(),
                        gateway.getPositionY(),
                        gateway.getA(),
                        gateway.getB()
                ))
                .collect(Collectors.toList());
    }

    public List<Asset> loadAssets() {
        return assets
                .stream()
                .map(asset -> new Asset(
                        asset.getId(),
                        asset.getLabel(),
                        asset.getKind()
                ))
                .collect(Collectors.toList());
    }

    public List<Staff> loadStaff() {
        return staff
                .stream()
                .map(staffCsv -> new Staff(
                        staffCsv.getId(),
                        staffCsv.getFirstName(),
                        staffCsv.getMiddleName(),
                        staffCsv.getLastName(),
                        staffCsv.getKind()
                ))
                .collect(Collectors.toList());
    }

    public String beaconMacForAsset(String assetId) {
        Optional<outland.emr.tracking.models.csv.Asset> asset = assets
                .stream()
                .filter(assetCsv -> assetCsv.getId().equals(assetId))
                .findFirst();
        if (asset.isEmpty()) return null;
        Optional<outland.emr.tracking.models.csv.Beacon> optionalBeacon = beacons
                .stream()
                .filter(beacon -> beacon.getId().equals(asset.get().getBeaconId()))
                .findFirst();
        if (optionalBeacon.isEmpty()) {
            return null;
        }
        return optionalBeacon.get().getMac();
    }

    public String beaconMacForStaff(String staffId) {
        Optional<outland.emr.tracking.models.csv.Staff> optionalStaff = staff
                .stream()
                .filter(staffCsv -> staffCsv.getId().equals(staffId))
                .findFirst();
        if (optionalStaff.isEmpty()) return null;
        Optional<outland.emr.tracking.models.csv.Beacon> optionalBeacon = beacons
                .stream()
                .filter(beacon -> beacon.getId().equals(optionalStaff.get().getBeaconId()))
                .findFirst();
        if (optionalBeacon.isEmpty()) {
            return null;
        }
        return  optionalBeacon.get().getMac();
    }

    public List<Long> getIdOfSiblingsForGatewayId(Long id) {
        Optional<Siblings> listSiblingsOptional = siblings
                .stream()
                .filter(sibling -> sibling.getGatewayId().equals(id))
                .findFirst();
        if (listSiblingsOptional.isEmpty()) {
            return new ArrayList<>();
        }
        return listSiblingsOptional.get().getSiblingsList();
    }

    private InputStreamReader getBeaconFile() {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("beacons.csv");
        assert file != null;
        return new InputStreamReader(file);
    }

    private InputStreamReader getGatewaysFile() {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("gateways.csv");
        assert file != null;
        return new InputStreamReader(file);
    }

    private InputStreamReader getAssetsFile() {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("assets.csv");
        assert file != null;
        return new InputStreamReader(file);
    }

    private InputStreamReader getStaffFile() {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("staff.csv");
        assert file != null;
        return new InputStreamReader(file);
    }

    private InputStreamReader getSiblingsFile() {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("siblings.tsv");
        assert file != null;
        return new InputStreamReader(file);
    }
}
