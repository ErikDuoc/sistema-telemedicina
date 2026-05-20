package cl.duoc.fullstack.agendaservice.service;

import cl.duoc.fullstack.agendaservice.client.DoctorClient;
import cl.duoc.fullstack.agendaservice.dto.*;
import cl.duoc.fullstack.agendaservice.model.Availability;
import cl.duoc.fullstack.agendaservice.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorClient doctorClient;

    public AvailabilityResponseDTO create(AvailabilityRequestDTO dto) {

        DoctorResponseDTO doctor = doctorClient.getDoctor(dto.getDoctorId());

        Availability availability = Availability.builder()
                .doctorId(dto.getDoctorId())
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        Availability saved = availabilityRepository.save(availability);

        return AvailabilityResponseDTO.builder()
                .id(saved.getId())
                .doctorId(saved.getDoctorId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .dayOfWeek(saved.getDayOfWeek())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .build();
    }

    public List<AvailabilityResponseDTO> getByDoctor(Long doctorId) {

        DoctorResponseDTO doctor = doctorClient.getDoctor(doctorId);

        return availabilityRepository.findByDoctorId(doctorId)
                .stream()
                .map(a -> AvailabilityResponseDTO.builder()
                        .id(a.getId())
                        .doctorId(a.getDoctorId())
                        .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                        .dayOfWeek(a.getDayOfWeek())
                        .startTime(a.getStartTime())
                        .endTime(a.getEndTime())
                        .build())
                .toList();
    }
}
