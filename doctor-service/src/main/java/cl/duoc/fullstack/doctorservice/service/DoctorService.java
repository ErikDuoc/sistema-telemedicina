package cl.duoc.fullstack.doctorservice.service;

import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.exception.DoctorNotFoundException;
import cl.duoc.fullstack.doctorservice.exception.DuplicateDoctorException;
import cl.duoc.fullstack.doctorservice.exception.SpecialtyNotFoundException;
import cl.duoc.fullstack.doctorservice.model.Doctor;
import cl.duoc.fullstack.doctorservice.model.Specialty;
import cl.duoc.fullstack.doctorservice.repository.DoctorRepository;
import cl.duoc.fullstack.doctorservice.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorResponseDTO createDoctor(DoctorRequestDTO dto) {

        if (doctorRepository.existsByNationalRegistry(dto.getNationalRegistry())) {
            throw new DuplicateDoctorException("Doctor already exists with this national registry");
        }

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new SpecialtyNotFoundException("Specialty not found with id: " + dto.getSpecialtyId()));

        Doctor doctor = Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalRegistry(dto.getNationalRegistry())
                .email(dto.getEmail())
                .specialty(specialty)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return mapToResponse(savedDoctor);
    }

    public List<DoctorResponseDTO> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DoctorResponseDTO getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));

        return mapToResponse(doctor);
    }

    private DoctorResponseDTO mapToResponse(Doctor doctor) {

        String specialtyName = doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null;

        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .nationalRegistry(doctor.getNationalRegistry())
                .email(doctor.getEmail())
                .specialtyName(specialtyName)
                .build();
    }
}
