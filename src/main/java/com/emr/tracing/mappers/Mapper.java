package com.emr.tracing.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mapper {
    @Autowired
    private final BeaconMapper beaconMapper;
    @Autowired
    private final PatientMapper patientMapper;

    public Mapper(BeaconMapper beaconMapper, PatientMapper patientMapper) {
        this.beaconMapper = beaconMapper;
        this.patientMapper = patientMapper;
    }

    @Bean()
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        modelMapper.addMappings(beaconMapper.getMappings());
        modelMapper.addMappings(patientMapper.getMappings());


        return modelMapper;
    }
}
