package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso que marca uma Notification como acknowledged (confirmada pelo morador).
 */
@Service
public class AcknowledgeNotificationUseCase {
    private static final Logger log = LoggerFactory.getLogger(AcknowledgeNotificationUseCase.class);

    private final NotificationRepository notificationRepository;
    private final com.fiap.notificationservice.infrastructure.kafka.NotificationProducer producer;

    public AcknowledgeNotificationUseCase(NotificationRepository notificationRepository,
                                          com.fiap.notificationservice.infrastructure.kafka.NotificationProducer producer) {
        this.notificationRepository = notificationRepository;
        this.producer = producer;
    }

    /**
     * Marca a notification como acknowledged e persiste. Retorna a entidade atualizada ou null caso não exista.
     */
    public Notification execute(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId);
        if (n == null) {
            log.warn("Confirmação solicitada para notificação inexistente id={}", notificationId);
            return null;
        }

        if (n.isAcknowledged()) {
            log.info("ID da notificação={} já reconhecida", notificationId);
            return n;
        }

        n.setAcknowledged(true);
        // persistir alteração
        Notification saved = notificationRepository.save(n);

        // publicar versão atualizada para o tópico de saída (facilita auditoria/reporting)
        try {
            producer.publish(saved);
        } catch (Exception e) {
            log.error("Falha ao publicar confirmação de notificação para id={}", saved.getId(), e);
        }

        log.info("ID da notificação={} reconhecido", saved.getId());
        return saved;
    }
}