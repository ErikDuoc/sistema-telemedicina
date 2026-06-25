package cl.duoc.fullstack.notificationservice.controller;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.service.NotificationService;
import cl.duoc.fullstack.notificationservice.service.NotificationLinkAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Notificaciones", description = "Operaciones para gestionar notificaciones del sistema")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationLinkAssembler notificationLinkAssembler;

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

    @Operation(summary = "Obtener historial de notificaciones", description = "Obtiene todas las notificaciones recibidas por un usuario específico con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/history/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<NotificationResponseDTO>>> getNotificationHistory(
            @PathVariable Long userId
    ) {
        List<EntityModel<NotificationResponseDTO>> notifications = notificationService.getNotificationHistory(userId).stream()
                .map(notificationLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<NotificationResponseDTO>> collection = CollectionModel.of(notifications);
        collection.add(linkTo(methodOn(NotificationController.class).getNotificationHistory(userId)).withSelfRel());

        return ResponseEntity.ok(collection);
    }
}
