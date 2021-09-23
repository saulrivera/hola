package outland.emr.tracking.controllers;

import outland.emr.tracking.dtos.BeaconDTO;
import outland.emr.tracking.logic.BeaconLogic;
import outland.emr.tracking.mappers.Mapper;
import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.socket.Stream;
import outland.emr.tracking.websockets.TracingSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/beacon")
public class BeaconController {
    @Autowired
    private final BeaconLogic beaconLogic;
    @Autowired
    private final Mapper mapper;
    @Autowired
    private final TracingSocket tracingSocket;

    public BeaconController(BeaconLogic beaconLogic, Mapper mapper, TracingSocket tracingSocket) {
        this.beaconLogic = beaconLogic;
        this.mapper = mapper;
        this.tracingSocket = tracingSocket;
    }

    @GetMapping("/available")
    public List<BeaconDTO> getAvailableBeacons() {
        return beaconLogic.getAvailable()
                .stream()
                .filter(beacon -> beacon.getType().equals(BeaconType.PATIENT))
                .map(beacon -> mapper.getMapper().map(beacon, BeaconDTO.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/associate")
    public void associate(@RequestParam String patientId, @RequestParam String uniqueId) {
        Stream stream = beaconLogic.associate(patientId, uniqueId);
        if (stream != null) {
            try {
                tracingSocket.emitBeaconUpdate(stream);
            } catch (IOException ignored) { }
        }
    }

    @PutMapping("/deassociate")
    public void deassociate(@RequestParam String beaconLabel) {
        Stream stream = beaconLogic.disassociate(beaconLabel);
        if (stream != null) {
            try {
                tracingSocket.emitBeaconDetachment(stream);
            } catch (IOException ignored) { }
        }
    }
}
