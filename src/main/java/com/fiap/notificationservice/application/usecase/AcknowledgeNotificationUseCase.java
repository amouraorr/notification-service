package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.infrastructure.kafka.NotificationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Caso de uso que marca uma Notification como acknowledged (confirmada pelo morador).
 */
@Service
public class AcknowledgeNotificationUseCase {
    private static final Logger log = LoggerFactory.getLogger(AcknowledgeNotificationUseCase.class);

    private final NotificationRepository notificationRepository;
    private final NotificationProducer producer;

    public AcknowledgeNotificationUseCase(NotificationRepository notificationRepository,
                                          NotificationProducer producer) {
        this.notificationRepository = notificationRepository;
        this.producer = producer;
    }

    /**
     * Marca a notification como acknowledged e persiste. Retorna a entidade atualizada ou null caso não exista.
     *
     * Regras adicionais:
     * - se status for null ou "PENDING" ao confirmar, atualiza para "SENT";
     * - se sentAt for nulo, registra OffsetDateTime.now();
     * - atualiza updatedAt sempre que houver alteração.
     */
    public Notification execute(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId);
        if (n == null) {
            log.warn("Acknowledge requested for non-existing notification id={}", notificationId);
            return null;
        }

        boolean changed = false;

        if (!n.isAcknowledged()) {
            n.setAcknowledged(true);
            changed = true;
        }

        String status = n.getStatus();
        if (status == null || status.trim().isEmpty() || "PENDING".equalsIgnoreCase(status)) {
            n.setStatus("SENT");
            if (n.getSentAt() == null) {
                n.setSentAt(OffsetDateTime.now());
            }
            changed = true;
        }

        n.setUpdatedAt(OffsetDateTime.now());
        changed = true;

        Notification saved = notificationRepository.save(n);

        try {
            producer.publish(saved);
        } catch (Exception e) {
            log.error("Falha ao publicar notification ack for id={}", saved.getId(), e);
        }

        if (saved.isAcknowledged()) {
            log.info("Notification id={} acknowledged (status={} sentAt={})", saved.getId(), saved.getStatus(), saved.getSentAt());
        } else {
            log.info("Notification id={} saved after ack attempt (acknowledged=false?)", saved.getId());
        }
        return saved;
    }
}