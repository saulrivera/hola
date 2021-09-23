package outland.emr.tracking.mappers;

import outland.emr.tracking.dtos.BeaconDTO;
import outland.emr.tracking.models.mongo.Beacon;
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
