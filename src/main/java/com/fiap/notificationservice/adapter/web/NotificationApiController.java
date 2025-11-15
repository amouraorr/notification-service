package com.fiap.notificationservice.adapter.web;

import com.fiap.notificationservice.adapter.web.mapper.NotificationWebMapper;
import com.fiap.notificationservice.application.dto.response.NotificationResponseDto;
import com.fiap.notificationservice.application.usecase.AcknowledgeNotificationUseCase;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API pública do serviço de notificações para consulta e confirmação (ack) por morador.
 */
@Tag(name = "Notification", description = "Endpoints para consulta e confirmação (ack) de notificações")
@RestController
@RequestMapping("/api/notification")
public class NotificationApiController {

    private static final Logger log = LoggerFactory.getLogger(NotificationApiController.class);

    private final NotificationRepository repository;
    private final AcknowledgeNotificationUseCase acknowledgeUseCase;
    private final NotificationWebMapper mapper;

    public NotificationApiController(NotificationRepository repository,
                                     AcknowledgeNotificationUseCase acknowledgeUseCase,
                                     NotificationWebMapper mapper) {
        this.repository = repository;
        this.acknowledgeUseCase = acknowledgeUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Obter notificação por ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getById(@PathVariable("id") UUID id) {
        Notification n = repository.findById(id);
        if (n == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toDto(n));
    }

    @Operation(summary = "Confirmar (ack) notificação por ID")
    @PostMapping("/{id}/ack")
    public ResponseEntity<NotificationResponseDto> acknowledge(@PathVariable("id") UUID id) {
        Notification updated = acknowledgeUseCase.execute(id);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toDto(updated));
    }
}