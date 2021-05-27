package com.emr.tracing.config;

import com.emr.tracing.logic.*;
import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.mongo.*;
import com.emr.tracing.models.neo4j.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public DataInit(
            BeaconLogic beaconLogic,
            PatientLogic patientLogic,
            GatewayLogic gatewayLogic,
            AssetLogic assetLogic,
            StaffLogic staffLogic
    ) {
        this.beaconLogic = beaconLogic;
        this.patientLogic = patientLogic;
        this.gatewayLogic = gatewayLogic;
        this.assetLogic = assetLogic;
        this.staffLogic = staffLogic;
    }

    @Bean
    public void loadData() {
        Beacon[] beacons = new Beacon[] {
                new Beacon("E38B580AE0A8", "EMRB1", BeaconType.PATIENT),
                new Beacon("FF5ECB2AF4FC", "EMRB2", BeaconType.PATIENT),
                new Beacon("C0E9E0B42457", "EMRB3", BeaconType.STAFF),
                new Beacon("FDF323E48926", "EMRB4", BeaconType.STAFF),
                new Beacon("DD4366C9EE97", "EMRB5", BeaconType.ASSET)
        };

        if (beaconLogic.isTableEmpty()) {
            Arrays.stream(beacons).forEach(beaconLogic::add);
        }

        if (patientLogic.isTableEmpty()) {
            Arrays.stream(new Patient[] {
                    new Patient("Saul", "Gerardo", "Rivera", "42", "123-456-7890", "super@outlandhq.com")
            }).forEach(patientLogic::add);
        }

        if (gatewayLogic.isTableEmpty()) {
            var gateway1 = new Gateway(0, "EMRG1", "68B9D3D1928C", 0, 820.0, 600.0);
            var gateway2 = new Gateway(1, "EMRG2", "68B9D3D196F0", 0, 100.0, 600.0);

            List.of(gateway1, gateway2).forEach(gatewayLogic::add);

            gateway1.getSiblings().add(gateway2);
            gatewayLogic.add(gateway1);
        }

        if (staffLogic.isTableEmpty()) {
            Staff[] staffs = new Staff[] {
                    new Staff("Diana", "Tenorio", "Bolanos", StaffKind.NURSE),
                    new Staff("Shlomi", "", "Saporta", StaffKind.PROVIDER)
            };
            Arrays.stream(staffs).forEach(staffLogic::add);

            staffLogic.associate(staffs[0], beacons[2].getMac());
            staffLogic.associate(staffs[1], beacons[3].getMac());
        }

        if (assetLogic.isTableEmpty()) {
            Asset asset = new Asset("EKG", AssetKind.MONITOR);
            assetLogic.add(asset);

            assetLogic.associate(asset, beacons[4].getMac());
        }

        beaconLogic.syncWithRedis();
        patientLogic.syncWithRedis();
        gatewayLogic.syncWithRedis();
        staffLogic.syncWithRedis();
        assetLogic.syncWithRedis();
    }
}
