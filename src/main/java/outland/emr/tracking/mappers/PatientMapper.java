package outland.emr.tracking.mappers;

import outland.emr.tracking.dtos.PatientDTO;
import outland.emr.tracking.logic.BeaconLogic;
import outland.emr.tracking.models.mongo.Beacon;
import outland.emr.tracking.models.mongo.Patient;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PatientMapper {
    @Autowired
    public BeaconLogic beaconLogic;

    public PatientMapper(BeaconLogic beaconLogic) {
        this.beaconLogic = beaconLogic;
    }

    private Converter<String, String> converter() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String source) {
                Beacon beacon = beaconLogic.getBeaconRelatedToPatientId(source);
                if (beacon == null) {
                    return "";
                }
                return Objects.requireNonNullElse(beacon.getLabel(), "");
            }
        };
    }

    public PropertyMap<Patient, PatientDTO> getMappings() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                using(converter()).map().setTrackingDeviceId(source.getId());
            }
        };
    }
}
