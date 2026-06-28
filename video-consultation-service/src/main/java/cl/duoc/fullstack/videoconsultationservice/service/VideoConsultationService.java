package cl.duoc.fullstack.videoconsultationservice.service;

import cl.duoc.fullstack.videoconsultationservice.client.AppointmentClient;
import cl.duoc.fullstack.videoconsultationservice.dto.*;
import cl.duoc.fullstack.videoconsultationservice.exception.DuplicateVideoConsultationException;
import cl.duoc.fullstack.videoconsultationservice.exception.VideoConsultationNotFoundException;
import cl.duoc.fullstack.videoconsultationservice.model.VideoConsultation;
import cl.duoc.fullstack.videoconsultationservice.repository.VideoConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoConsultationService {

    private final VideoConsultationRepository repository;
    private final AppointmentClient appointmentClient;

    public VideoConsultationResponseDTO create(VideoConsultationRequestDTO request){

        if (repository.existsByAppointmentId(request.getAppointmentId())) {
            throw new DuplicateVideoConsultationException("Ya existe una videoconsulta para la cita: " + request.getAppointmentId());
        }

        appointmentClient.validateAppointment(request.getAppointmentId());

        String meetingUrl = "https://telemedicina.cl/meeting/" + UUID.randomUUID();

        VideoConsultation consultation = VideoConsultation.builder()
                .appointmentId(request.getAppointmentId())
                .meetingUrl(meetingUrl)
                .platform(request.getPlatform())
                .status("SCHEDULED")
                .createdAt(LocalDateTime.now().toString())
                .build();

        return map(repository.save(consultation));
    }

    public VideoConsultationResponseDTO getById(Long id){

        VideoConsultation consultation = repository.findById(id)
                .orElseThrow(() -> new VideoConsultationNotFoundException("Video consultation not found"));

        return map(consultation);
    }

    private VideoConsultationResponseDTO map(VideoConsultation v){
        return VideoConsultationResponseDTO.builder()
                .id(v.getId())
                .appointmentId(v.getAppointmentId())
                .meetingUrl(v.getMeetingUrl())
                .platform(v.getPlatform())
                .status(v.getStatus())
                .createdAt(v.getCreatedAt())
                .build();
    }
}