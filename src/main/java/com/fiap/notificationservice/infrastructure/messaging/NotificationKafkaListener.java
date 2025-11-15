package com.fiap.notificationservice.infrastructure.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.fiap.notificationservice.infrastructure.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public NotificationKafkaListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${KAFKA_TOPICS_PARCELS_IN:parcels.received}", groupId = "${KAFKA_CONSUMER_GROUP_ID:notification-service}")
    public void onMessage(@Payload String payload) {
        try {
            Map<String, Object> evt = objectMapper.readValue(payload, Map.class);
            log.info("Mensagem recebida no notification-service: preview={}", payload.length() > 600 ? payload.substring(0,600) + "...": payload);
            notificationService.handleParcelEvent(evt);
        } catch (Exception e) {
            log.error("Erro ao processar payload recebido", e);
        }
    }
}