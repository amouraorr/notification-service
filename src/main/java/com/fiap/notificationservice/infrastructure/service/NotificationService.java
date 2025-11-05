package com.fiap.notificationservice.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.domain.model.Notification;

import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

/**
 * Service que processa o payload do Kafka (map) e tenta enviar via provider mock, persistir e publicar evento de saída.
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final RestTemplate rest;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String externalProvidersBase;

    public NotificationService(NotificationRepository repository,
                               ObjectMapper objectMapper,
                               KafkaTemplate<String, String> kafkaTemplate,
                               org.springframework.core.env.Environment env) {
        this.repository = repository;
        this.rest = new RestTemplate();
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.externalProvidersBase = env.getProperty("EXTERNAL_PROVIDERS_MOCK", "http://localhost:9000");
    }

    public void handleParcelEvent(Map<String, Object> event) {
        Long parcelId = event.get("parcelId") == null ? null : Long.valueOf(String.valueOf(event.get("parcelId")));
        String channel = event.getOrDefault("channel", "PUSH").toString();
        String contact = event.getOrDefault("contact", "").toString();
        String desc = event.getOrDefault("description", "").toString();

        Notification n = new Notification();

        n.setId(UUID.randomUUID());

        n.setParcelId(parcelId);
        n.setResidentName(String.valueOf(event.getOrDefault("residentName", "")));
        n.setApartment(String.valueOf(event.getOrDefault("apartment", "")));
        n.setContact(contact);
        n.setChannel(channel);
        n.setDescription(desc);

        OffsetDateTime createdAt = parseReceivedAt(event.get("receivedAt"));
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        n.setCreatedAt(createdAt);

        try {
            // Simula envio via provider mock
            if ("SMS".equalsIgnoreCase(channel)) {
                var resp = rest.postForEntity(externalProvidersBase + "/sms", Map.of("to", contact, "message", desc), String.class);
                n.setStatus("SENT");
                n.setResultDetail("sms:" + resp.getStatusCodeValue());
            } else if ("EMAIL".equalsIgnoreCase(channel)) {
                var resp = rest.postForEntity(externalProvidersBase + "/email", Map.of("to", contact, "subject", "Nova encomenda", "body", desc), String.class);
                n.setStatus("SENT");
                n.setResultDetail("email:" + resp.getStatusCodeValue());
            } else {
                // PUSH / default: apenas registra e considera como SENT
                n.setStatus("SENT");
                n.setResultDetail("push:ok");
            }
        } catch (Exception ex) {
            log.error("Erro ao enviar notificação para parcelId={} contact={} channel={}", parcelId, contact, channel, ex);
            n.setStatus("FAILED");
            n.setResultDetail(ex.getMessage());
        }

        // Persiste a notification
        try {
            repository.save(n);
        } catch (Exception ex) {
            // Informação adicional no log para diagnóstico
            log.error("Falha ao salvar Notification (id={} parcelId={}). Verifique mapeamento JPA/schema e se a coluna id aceita UUID. Exception:", n.getId(), parcelId, ex);
        }

        // Publica evento de saída para tópico de notifications.sent (auditoria)
        try {
            Map<String, Object> out = Map.of(
                    "eventType", "NOTIFICATION_SENT",
                    "parcelId", parcelId,
                    "notificationId", n.getId(),
                    "status", n.getStatus(),
                    "resultDetail", n.getResultDetail(),
                    "sentAt", OffsetDateTime.now().toString()
            );
            String payload = objectMapper.writeValueAsString(out);
            String topic = java.util.Optional.ofNullable(System.getenv("KAFKA_TOPICS_NOTIFICATIONS_OUT")).orElse("notifications.sent");
            kafkaTemplate.send(topic, String.valueOf(parcelId), payload);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de NOTIFICATION_SENT", e);
        }
    }

    /**
     * Interpretar diferentes formatos possíveis do campo receivedAt enviado no payload (String ISO ou OffsetDateTime serializado).
     */
    private OffsetDateTime parseReceivedAt(Object receivedAtObj) {
        if (receivedAtObj == null) return null;
        try {
            if (receivedAtObj instanceof OffsetDateTime) {
                return (OffsetDateTime) receivedAtObj;
            } else {
                String s = String.valueOf(receivedAtObj);
                if (s.isBlank()) return null;
                return OffsetDateTime.parse(s);
            }
        } catch (DateTimeParseException ex) {
            log.warn("receivedAt presente porém não pôde ser parseado: '{}'", receivedAtObj, ex);
            return null;
        } catch (Exception ex) {
            log.warn("Erro inesperado ao parsear receivedAt: '{}'", receivedAtObj, ex);
            return null;
        }
    }
}