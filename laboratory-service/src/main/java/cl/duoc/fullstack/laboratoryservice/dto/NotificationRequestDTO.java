package cl.duoc.fullstack.laboratoryservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationRequestDTO {

    private Long recipientId;
    private String type;
    private String recipient;
    private String message;
}
