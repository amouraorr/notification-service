package com.fiap.notificationservice.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

/**
 * Service que processa o payload do Kafka (map) e enviar via provider mock, persistir e publicar evento de saída.
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
        this.externalProvidersBase = env.getProperty("EXTERNAL_PROVIDERS_MOCK", "http://mock-providers");
    }

    public void handleParcelEvent(Map<String, Object> event) {
        Long parcelId = event.get("parcelId") == null ? null : Long.valueOf(String.valueOf(event.get("parcelId")));
        String channel = event.getOrDefault("channel", "PUSH") == null ? "PUSH" : event.getOrDefault("channel", "PUSH").toString();
        String contactRaw = event.getOrDefault("contact", "") == null ? "" : event.getOrDefault("contact", "").toString();
        String desc = event.getOrDefault("description", "") == null ? "" : event.getOrDefault("description", "").toString();

        Notification n = new Notification();

        n.setId(UUID.randomUUID());

        n.setParcelId(parcelId);
        n.setResidentName(String.valueOf(event.getOrDefault("residentName", "")));
        n.setApartment(String.valueOf(event.getOrDefault("apartment", "")));

        String contact = contactRaw != null && !contactRaw.isBlank() ? contactRaw : null;
        n.setContact(contact);

        n.setChannel(channel != null ? channel.toUpperCase() : "PUSH");

        n.setMessage(buildMessage(n.getResidentName(), n.getApartment(), desc));

        OffsetDateTime createdAt = parseReceivedAt(event.get("receivedAt"));
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        n.setCreatedAt(createdAt);

        n.setAcknowledged(false);

        n.setStatus("PENDING");
        n.setResultDetail(null);

        try {
            if ("SMS".equalsIgnoreCase(channel) || "EMAIL".equalsIgnoreCase(channel)) {
                if (contact == null) {
                    log.warn("Payload para parcelId={} channel={} não contém contact — pulando envio externo e persistindo PENDING", parcelId, channel);
                    n.setStatus("PENDING");
                    n.setResultDetail("no-contact");
                } else {

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    if ("SMS".equalsIgnoreCase(channel)) {
                        String url = externalProvidersBase + "/sms";
                        Map<String, Object> body = Map.of("to", contact, "message", n.getMessage());
                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                        try {
                            ResponseEntity<String> resp = rest.postForEntity(url, request, String.class);
                            n.setStatus("SENT");
                            n.setResultDetail("sms:" + resp.getStatusCodeValue());
                            n.setSentAt(OffsetDateTime.now());
                        } catch (HttpClientErrorException hce) {
                            log.error("HTTP error ao enviar SMS parcelId={} contact={} url={} status={}", parcelId, contact, url, hce.getStatusCode(), hce);
                            n.setStatus("FAILED");
                            String bodyStr = hce.getResponseBodyAsString();
                            n.setResultDetail("http:" + hce.getStatusCode().value() + ":" + (bodyStr != null ? truncate(bodyStr, 300) : ""));
                        }
                    } else {
                        String url = externalProvidersBase + "/email";
                        Map<String, Object> body = Map.of("to", contact, "subject", "Nova encomenda", "body", n.getMessage());
                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                        try {
                            ResponseEntity<String> resp = rest.postForEntity(url, request, String.class);
                            n.setStatus("SENT");
                            n.setResultDetail("email:" + resp.getStatusCodeValue());
                            n.setSentAt(OffsetDateTime.now());
                        } catch (HttpClientErrorException hce) {
                            log.error("HTTP error ao enviar EMAIL parcelId={} contact={} url={} status={}", parcelId, contact, url, hce.getStatusCode(), hce);
                            n.setStatus("FAILED");
                            String bodyStr = hce.getResponseBodyAsString();
                            n.setResultDetail("http:" + hce.getStatusCode().value() + ":" + (bodyStr != null ? truncate(bodyStr, 300) : ""));
                        }
                    }
                }
            } else {
                n.setStatus("SENT");
                n.setResultDetail("push:ok");
                n.setSentAt(OffsetDateTime.now());
            }
        } catch (RestClientException ex) {
            log.error("Erro ao enviar notificação para parcelId={} contact={} channel={}", parcelId, contact, channel, ex);
            n.setStatus("FAILED");
            n.setResultDetail(ex.getClass().getSimpleName() + ":" + ex.getMessage());
        } catch (Exception ex) {
            log.error("Erro inesperado ao enviar notificação para parcelId={} contact={} channel={}", parcelId, contact, channel, ex);
            n.setStatus("FAILED");
            n.setResultDetail(ex.getClass().getSimpleName() + ":" + ex.getMessage());
        }

        try {
            repository.save(n);
        } catch (Exception ex) {
            log.error("Falha ao salvar Notification (id={} parcelId={}). Verifique mapeamento JPA/schema e se a coluna id aceita UUID.", n.getId(), parcelId, ex);
        }

        try {
            Map<String, Object> out = Map.of(
                    "eventType", "NOTIFICATION_SENT",
                    "parcelId", parcelId,
                    "notificationId", n.getId(),
                    "status", n.getStatus(),
                    "resultDetail", n.getResultDetail(),
                    "sentAt", n.getSentAt() != null ? n.getSentAt().toString() : OffsetDateTime.now().toString()
            );
            String payload = objectMapper.writeValueAsString(out);
            String topic = java.util.Optional.ofNullable(System.getenv("KAFKA_TOPICS_NOTIFICATIONS_OUT")).orElse("notifications.sent");
            kafkaTemplate.send(topic, String.valueOf(parcelId), payload);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de NOTIFICATION_SENT", e);
        }
    }

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

    private String buildMessage(String residentName, String apartment, String desc) {
        String rn = residentName != null ? residentName : "";
        String ap = apartment != null ? apartment : "";
        String d = desc != null ? desc : "";
        return "Encomenda recebida para " + rn + " apto " + ap + (d.isBlank() ? "" : " — " + d);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}