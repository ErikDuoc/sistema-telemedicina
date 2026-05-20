package cl.duoc.fullstack.notificationservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateDTO {

    private Long id;

    private String name;

    private String content;
}
