package com.emr.tracing.config;

import com.emr.tracing.logic.*;
import com.emr.tracing.managers.DataTranslation;
import com.emr.tracing.models.csv.Siblings;
import com.emr.tracing.models.mongo.*;
import com.emr.tracing.models.neo4j.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DataInit {
    @Autowired
    private final BeaconLogic beaconLogic;
    @Autowired
    private final PatientLogic patientLogic;
    @Autowired
    private final GatewayLogic gatewayLogic;
    @Autowired
    private final AssetLogic assetLogic;
    @Autowired
    private final StaffLogic staffLogic;
    @Autowired
    private final RecordStateLogic recordStateLogic;
    @Autowired
    private final DataTranslation translation;

    public DataInit(
            BeaconLogic beaconLogic,
            PatientLogic patientLogic,
            GatewayLogic gatewayLogic,
            AssetLogic assetLogic,
            StaffLogic staffLogic,
            RecordStateLogic recordStateLogic,
            DataTranslation translation
    ) {
        this.beaconLogic = beaconLogic;
        this.patientLogic = patientLogic;
        this.gatewayLogic = gatewayLogic;
        this.assetLogic = assetLogic;
        this.staffLogic = staffLogic;
        this.recordStateLogic = recordStateLogic;
        this.translation = translation;
    }

    @Bean
    public void loadData() {
        List<Beacon> beacons = translation.loadBeacons();

        if (beaconLogic.isTableEmpty()) {
            beacons.forEach(beaconLogic::add);
        }

        List<Gateway> gateways = translation.loadGateways();
        if (gatewayLogic.isTableEmpty()) {
            gateways.forEach(gatewayLogic::add);

            // Solve for siblings relationships
            for (Gateway gateway : gateways) {
                List<Long> siblingsIds = translation.getIdOfSiblingsForGatewayId(gateway.getId());
                List<Gateway> siblings = gateways
                        .stream()
                        .filter(gat -> siblingsIds.contains(gat.getId()))
                        .collect(Collectors.toList());
                gateway.getSiblings().addAll(siblings);
                gatewayLogic.add(gateway);
            }
        }

        List<Asset> assets = translation.loadAssets();
        if (assetLogic.isTableEmpty()) {
            assets.forEach(assetLogic::add);

            assets.forEach(asset -> assetLogic.associate(asset, translation.beaconMacForAsset(asset.getId())));
        }

        List<Staff> staff = translation.loadStaff();
        if (staffLogic.isTableEmpty()) {
            staff.forEach(staffLogic::add);

            for (Staff staffElement : staff) {
                staffLogic.associate(staffElement, translation.beaconMacForStaff(staffElement.getId()));
            }
        }

        if (patientLogic.isTableEmpty()) {
            Arrays.stream(new Patient[] {
                    new Patient("Saul", "Gerardo", "Rivera", "42", "123-456-7890", "super@outlandhq.com")
            }).forEach(patientLogic::add);
        }

        beaconLogic.syncWithRedis();
        patientLogic.syncWithRedis();
        gatewayLogic.syncWithRedis();
        staffLogic.syncWithRedis();
        assetLogic.syncWithRedis();
        recordStateLogic.flushTable();
    }
}
