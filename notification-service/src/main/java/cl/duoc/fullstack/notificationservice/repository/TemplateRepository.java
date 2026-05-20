package cl.duoc.fullstack.notificationservice.repository;

import cl.duoc.fullstack.notificationservice.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {
}
