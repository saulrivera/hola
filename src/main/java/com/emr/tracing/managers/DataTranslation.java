package com.emr.tracing.managers;

import com.emr.tracing.models.csv.*;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DataTranslation {
    private final List<Beacon> beacons;
    private final List<Gateway> gateways;
    private final List<Asset> assets;
    private final List<Staff> staff;
    private final List<Siblings> siblings;

    public DataTranslation() {
        beacons = new CsvToBeanBuilder(getBeaconFile())
                .withType(Beacon.class)
                .build()
                .parse();
        gateways = new CsvToBeanBuilder(getGatewaysFile())
                .withType(Gateway.class)
                .build()
                .parse();
        assets = new CsvToBeanBuilder(getAssetsFile())
                .withType(Asset.class)
                .build()
                .parse();
        staff = new CsvToBeanBuilder(getStaffFile())
                .withType(Staff.class)
                .build()
                .parse();
        siblings = new CsvToBeanBuilder(getSiblingsFile())
                .withSeparator('\t')
                .withType(Siblings.class)
                .build()
                .parse();
    }

    public List<com.emr.tracing.models.mongo.Beacon> loadBeacons() {
        return beacons
                .stream()
                .map(beacon -> new com.emr.tracing.models.mongo.Beacon(
                        beacon.getId(),
                        beacon.getMac(),
                        beacon.getLabel(),
                        beacon.getType())
                )
                .collect(Collectors.toList());
    }

    public List<com.emr.tracing.models.neo4j.Gateway> loadGateways() {
        return gateways
                .stream()
                .map(gateway -> new com.emr.tracing.models.neo4j.Gateway(
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

    public List<com.emr.tracing.models.mongo.Asset> loadAssets() {
        return assets
                .stream()
                .map(asset -> new com.emr.tracing.models.mongo.Asset(
                        asset.getId(),
                        asset.getLabel(),
                        asset.getKind()
                ))
                .collect(Collectors.toList());
    }

    public List<com.emr.tracing.models.mongo.Staff> loadStaff() {
        return staff
                .stream()
                .map(staffCsv -> new com.emr.tracing.models.mongo.Staff(
                        staffCsv.getId(),
                        staffCsv.getFirstName(),
                        staffCsv.getMiddleName(),
                        staffCsv.getLastName(),
                        staffCsv.getKind()
                ))
                .collect(Collectors.toList());
    }

    public String beaconMacForAsset(String assetId) {
        Optional<Asset> asset = assets
                .stream()
                .filter(assetCsv -> assetCsv.getId().equals(assetId))
                .findFirst();
        if (asset.isEmpty()) return null;
        Optional<Beacon> optionalBeacon = beacons
                .stream()
                .filter(beacon -> beacon.getId().equals(asset.get().getBeaconId()))
                .findFirst();
        if (optionalBeacon.isEmpty()) {
            return null;
        }
        return optionalBeacon.get().getMac();
    }

    public String beaconMacForStaff(String staffId) {
        Optional<Staff> optionalStaff = staff
                .stream()
                .filter(staffCsv -> staffCsv.getId().equals(staffId))
                .findFirst();
        if (optionalStaff.isEmpty()) return null;
        Optional<Beacon> optionalBeacon = beacons
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

    private FileReader getBeaconFile() {
        try {
            File file = ResourceUtils.getFile("classpath:beacons.csv");
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileReader getGatewaysFile() {
        try {
            File file = ResourceUtils.getFile("classpath:gateways.csv");
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileReader getAssetsFile() {
        try {
            File file = ResourceUtils.getFile("classpath:assets.csv");
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileReader getStaffFile() {
        try {
            File file = ResourceUtils.getFile("classpath:staff.csv");
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileReader getSiblingsFile() {
        try {
            File file = ResourceUtils.getFile("classpath:siblings.tsv");
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
