package cl.duoc.fullstack.videoconsultationservice.repository;

import cl.duoc.fullstack.videoconsultationservice.model.VideoConsultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoConsultationRepository extends JpaRepository<VideoConsultation, Long> {
}