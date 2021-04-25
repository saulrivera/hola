package com.emr.tracing.mappers;

import com.emr.tracing.dtos.BeaconDTO;
import com.emr.tracing.models.mongo.Beacon;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BeaconMapper {
    public PropertyMap<Beacon, BeaconDTO> getMappings() {
        return new PropertyMap<Beacon, BeaconDTO>() {
            @Override
            protected void configure() {
                map().setUniqueId(source.getLabel());
            }
        };
    }
}
