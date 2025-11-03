package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.domain.port.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ProcessParcelEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessParcelEventUseCase.class);

    private final NotificationRepository repository;
    private final NotificationSender sender;

    public ProcessParcelEventUseCase(NotificationRepository repository, NotificationSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    /**
     * Constrói e envia (via NotificationSender) a Notification a partir do ParcelEventDto.
     * NotificationSender (NotificationSenderAdapter) é responsável por persistir e publicar em Kafka.
     */
    public void execute(ParcelEventDto dto) {
        if (dto == null) {
            log.warn("ParcelEventDto nulo — ignorando");
            return;
        }

        try {
            Notification n = new Notification();
            n.setId(UUID.randomUUID());
            n.setResidentName(dto.residentName);
            n.setApartment(dto.apartment);
            n.setContact(dto.contact);
            String channel = dto.channel;
            n.setChannel(channel == null || channel.isBlank() ? "PUSH" : channel.toUpperCase());
            n.setMessage(buildMessage(dto));
            n.setCreatedAt(dto.receivedAt != null ? dto.receivedAt : OffsetDateTime.now());
            n.setAcknowledged(false);

            sender.send(n);

            log.info("Notification created and sent for resident={} apt={}", n.getResidentName(), n.getApartment());
        } catch (Exception e) {
            log.error("Erro ao processar ParcelEventDto", e);

            try {
                Notification fallback = new Notification();
                fallback.setId(UUID.randomUUID());
                fallback.setResidentName(dto.residentName);
                fallback.setApartment(dto.apartment);
                fallback.setContact(dto.contact);
                fallback.setChannel(dto.channel == null ? "PUSH" : dto.channel.toUpperCase());
                fallback.setMessage(buildMessage(dto));
                fallback.setCreatedAt(dto.receivedAt != null ? dto.receivedAt : OffsetDateTime.now());
                fallback.setAcknowledged(false);
                repository.save(fallback);
            } catch (Exception ex) {
                log.error("Falha no fallback ao salvar Notification", ex);
            }
        }
    }

    private String buildMessage(ParcelEventDto dto) {
        String desc = dto.description != null ? dto.description : "";
        return "Encomenda recebida para " + dto.residentName + " apto " + dto.apartment + (desc.isBlank() ? "" : " — " + desc);
    }
}