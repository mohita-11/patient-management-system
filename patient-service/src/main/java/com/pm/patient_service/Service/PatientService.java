package com.pm.patient_service.Service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientNotFoundException;
import com.pm.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patientList = patientRepository.findAll();

        return patientList.stream()
                .map(PatientMapper::toDTO).collect(Collectors.toList());
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        //business logic - email should be unique
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail()))
        {
            throw new EmailAlreadyExistsException(patientRequestDTO.getEmail());
        }

        Patient patient = patientRepository.save(PatientMapper.toEntity(patientRequestDTO));

        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id.toString()));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id))
        {
            throw new EmailAlreadyExistsException(patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

}
