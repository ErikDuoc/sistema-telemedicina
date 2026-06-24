package cl.duoc.fullstack.notificationservice.controller;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificaciones", description = "Operaciones para gestionar notificaciones del sistema")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Enviar notificación", description = "Envía una nueva notificación a un destinatario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación enviada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDTO sendNotification(
            @Valid @RequestBody NotificationRequestDTO dto
    ) {

        return notificationService.sendNotification(dto);
    }

    @Operation(summary = "Obtener historial de notificaciones", description = "Obtiene todas las notificaciones recibidas por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/history/{userId}")
    public List<NotificationResponseDTO> getNotificationHistory(
            @PathVariable Long userId
    ) {

        return notificationService.getNotificationHistory(userId);
    }
}
