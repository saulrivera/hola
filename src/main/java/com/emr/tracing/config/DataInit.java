package com.emr.tracing.config;

import com.emr.tracing.logic.BeaconLogic;
import com.emr.tracing.logic.GatewayLogic;
import com.emr.tracing.logic.PatientLogic;
import com.emr.tracing.models.mongo.Beacon;
import com.emr.tracing.models.mongo.Patient;
import com.emr.tracing.models.neo4j.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Configuration
public class DataInit {
    @Autowired
    private final BeaconLogic beaconLogic;
    @Autowired
    private final PatientLogic patientLogic;
    @Autowired
    private final GatewayLogic gatewayLogic;

    public DataInit(BeaconLogic beaconLogic, PatientLogic patientLogic, GatewayLogic gatewayLogic) {
        this.beaconLogic = beaconLogic;
        this.patientLogic = patientLogic;
        this.gatewayLogic = gatewayLogic;
    }

    @Bean
    public void loadData() {
        if (beaconLogic.isTableEmpty()) {
            Arrays.stream(new Beacon[] {
                    new Beacon("C9494755A35C", "EMRB1"),
                    new Beacon("FF5ECB2AF4FC", "EMRB2"),
                    new Beacon("C0E9E0B42457", "EMRB3"),
                    new Beacon("FDF323E48926", "EMRB4"),
                    new Beacon("DD4366C9EE97", "EMRB5")
            }).forEach(beaconLogic::add);
        }

        if (patientLogic.isTableEmpty()) {
            Arrays.stream(new Patient[] {
                    new Patient("Saul", "Gerardo", "Rivera", "42", "123-456-7890", "super@outlandhq.com")
            }).forEach(patientLogic::add);
        }

        if (gatewayLogic.isTableEmpty()) {
            var gateways = Arrays.stream(new Gateway[] {
                    new Gateway("EMRG1", "68B9D3D1928C", 0, 10.0, 7.0),
                    new Gateway("EMRG2", "68B9D3D196F0", 0, -10.0, 7.0)
            }).collect(Collectors.toList());

            gateways.forEach(gatewayLogic::add);

            var siblings1 = new HashSet<Gateway>();
            siblings1.add(gateways.get(1));
            gateways.get(0).setSiblings(siblings1);

            gateways.forEach(gatewayLogic::add);
        }

        beaconLogic.syncWithRedis();
        patientLogic.syncWithRedis();
        gatewayLogic.syncWithRedis();
    }
}
