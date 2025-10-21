package com.fiap.encomendas.notificationservice.application.usecase;

import com.fiap.encomendas.notificationservice.application.dto.ParcelEventDto;
import com.fiap.encomendas.notificationservice.domain.model.Notification;
import com.fiap.encomendas.notificationservice.domain.port.NotificationRepository;
import com.fiap.encomendas.notificationservice.domain.port.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Caso de uso que processa eventos de encomenda: cria e persiste uma Notification e dispara o envio.
 */
@Service
public class ProcessParcelEventUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProcessParcelEventUseCase.class);

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    public ProcessParcelEventUseCase(NotificationRepository notificationRepository, NotificationSender notificationSender) {
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
    }

    public Notification execute(ParcelEventDto event) {
        log.info("Processando parcel event for resident={} apt={}", event.residentName, event.apartment);
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setResidentName(event.residentName);
        notification.setApartment(event.apartment);
        notification.setContact(event.contact);
        notification.setChannel(event.channel != null ? event.channel : "PUSH");
        notification.setMessage("Você recebeu uma encomenda: " + event.description);
        notification.setCreatedAt(event.receivedAt != null ? event.receivedAt : OffsetDateTime.now());
        notification.setAcknowledged(false);

        // persiste (porta)
        Notification saved = notificationRepository.save(notification);

        // envia (porta)
        notificationSender.send(saved);

        log.info("Notification created and send requested id={}", saved.getId());
        return saved;
    }
}