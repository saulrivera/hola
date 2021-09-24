package outland.emr.tracking.controllers;

import outland.emr.tracking.dtos.PatientDTO;
import outland.emr.tracking.managers.StreamManager;
import outland.emr.tracking.mappers.Mapper;
import outland.emr.tracking.logic.PatientLogic;
import outland.emr.tracking.models.mongo.Patient;
import outland.emr.tracking.websockets.TrackingSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private final Mapper mapper;
    @Autowired
    private final PatientLogic patientLogic;
    @Autowired
    private final TrackingSocket trackingSocket;
    @Autowired
    private final StreamManager streamManager;

    public PatientController(Mapper mapper, PatientLogic patientLogic, TrackingSocket trackingSocket, StreamManager streamManager) {
        this.mapper = mapper;
        this.patientLogic = patientLogic;
        this.trackingSocket = trackingSocket;
        this.streamManager = streamManager;
    }

    @GetMapping()
    public List<PatientDTO> getPatients() {
        List<Patient> patients = patientLogic.getAll();
        return patients
                .stream()
                .map(patient -> mapper.getMapper().map(patient, PatientDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public PatientDTO addPatient(@RequestBody PatientDTO patientDTO) {
        Patient patient = mapper.getMapper().map(patientDTO, Patient.class);
        patientLogic.add(patient);
        return mapper.getMapper().map(patient, PatientDTO.class);
    }

    @PutMapping()
    public PatientDTO update(@RequestBody PatientDTO patientDTO) throws Exception {
        Patient patient = mapper.getMapper().map(patientDTO, Patient.class);
        Patient updatedPatient = patientLogic.update(patient);

        trackingSocket.emitBeaconUpdate(streamManager.createStreamForPatient(patient.getId()));

        return mapper.getMapper().map(updatedPatient, PatientDTO.class);
    }

    @GetMapping("/searchByName")
    public List<PatientDTO> searchByName(@RequestParam String name) {
        List<Patient> foundPatients = patientLogic.searchByName(name);
        return foundPatients
                .stream()
                .map(patient -> mapper.getMapper().map(patient, PatientDTO.class))
                .collect(Collectors.toList());
    }
}
