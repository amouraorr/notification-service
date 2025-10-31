package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.domain.port.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Locale;
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

    /**
     * Processa o evento de encomenda vindo do Parcel Service.
     * Retorna a Notification persistida, ou null se o evento for inválido.
     *
     * Passos aplicados:
     * - valida contact (telefone/email) obrigatório para envio
     * - normaliza channel (EMAIL,SMS,PUSH) com default PUSH
     * - cria objeto Notification, persiste e solicita envio
     */
    public Notification execute(ParcelEventDto event) {
        if (event == null) {
            log.warn("Received null parcel event, ignoring");
            return null;
        }

        // validações básicas do negócio
        if (event.contact == null || event.contact.trim().isEmpty()) {
            log.warn("Parcel event has no contact; residentName={} apartment={}. Skipping notification.", event.residentName, event.apartment);
            return null;
        }

        String channel = (event.channel == null || event.channel.trim().isEmpty()) ? "PUSH" : event.channel.trim().toUpperCase(Locale.ROOT);
        // aceita apenas canais conhecidos; default PUSH
        if (!"EMAIL".equals(channel) && !"SMS".equals(channel) && !"PUSH".equals(channel)) {
            log.warn("Unknown channel '{}', defaulting to PUSH", event.channel);
            channel = "PUSH";
        }

        log.info("Processando parcel event for resident={} apt={} via {}", event.residentName, event.apartment, channel);
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setResidentName(event.residentName);
        notification.setApartment(event.apartment);
        notification.setContact(event.contact);
        notification.setChannel(channel);
        String desc = event.description != null ? event.description : "encomenda";
        notification.setMessage(String.format("Você recebeu uma %s. Destinatário: %s, apto: %s", desc, event.residentName, event.apartment));
        notification.setCreatedAt(event.receivedAt != null ? event.receivedAt : OffsetDateTime.now());
        notification.setAcknowledged(false);

        // persiste (porta)
        Notification saved = notificationRepository.save(notification);

        // envia (porta) - adapter se responsabiliza por atualizar sentAt e publicar se for o caso
        try {
            notificationSender.send(saved);
        } catch (Exception ex) {
            // deixar persistido e logar o erro; envio pode ser re-tentado por outros mecanismos se necessário
            log.error("Erro ao solicitar envio de notification id={}", saved.getId(), ex);
        }

        log.info("Notification created and send requested id={}", saved.getId());
        return saved;
    }
}