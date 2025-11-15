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

        log.debug("Envio de notificação (antes do provedor): id={} resident={} apt={} contact={} channel={}",
                notification.getId(), notification.getResidentName(), notification.getApartment(), notification.getContact(), notification.getChannel());


        try {
            mockProvider.send(notification);
        } catch (Exception e) {
            log.error("O MockProvider lançou uma exceção para a notificação id={}", notification.getId(), e);
        }

        notification.setSentAt(OffsetDateTime.now());
        try {
            repository.save(notification);
            log.debug("A notificação persistiu após o envio.: id={}", notification.getId());
        } catch (Exception e) {

            log.error("Falha ao salvar notification id={} após envio. Verifique o mapeamento JPA / schema do banco.", notification.getId(), e);

        }

        try {
            producer.publish(notification);
        } catch (Exception e) {
            log.error("Falha ao publicar notification event id={}", notification.getId(), e);
        }
    }
}