package com.fiap.notificationservice.infrastructure.provider;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationSender;
import com.fiap.notificationservice.infrastructure.kafka.NotificationProducer;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Adapter que implementa NotificationSender usando provider mock + publica em Kafka.
 */
@Component
public class NotificationSenderAdapter implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(NotificationSenderAdapter.class);

    private final MockProvider mockProvider;
    private final NotificationProducer producer;
    private final NotificationRepository repository;

    public NotificationSenderAdapter(MockProvider mockProvider, NotificationProducer producer, NotificationRepository repository) {
        this.mockProvider = mockProvider;
        this.producer = producer;
        this.repository = repository;
    }

    @Override
    public void send(Notification notification) {
        // chama provider externo (mock)
        mockProvider.send(notification);

        // marca sentAt e persiste atualização
        notification.setSentAt(OffsetDateTime.now());
        repository.save(notification);

        // publica evento de notificação em Kafka
        try {
            producer.publish(notification);
        } catch (Exception e) {
            log.error("Falha ao publicar notification event", e);
        }
    }
}