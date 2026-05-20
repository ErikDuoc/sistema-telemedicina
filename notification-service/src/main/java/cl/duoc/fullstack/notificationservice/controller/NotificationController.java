package cl.duoc.fullstack.notificationservice.controller;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDTO sendNotification(
            @Valid @RequestBody NotificationRequestDTO dto
    ) {

        return notificationService.sendNotification(dto);
    }

    @GetMapping("/history/{userId}")
    public List<NotificationResponseDTO> getNotificationHistory(
            @PathVariable Long userId
    ) {

        return notificationService.getNotificationHistory(userId);
    }
}
