package com.fiap.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Producer que publica Notification para tópico de saída.
 */
@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${notification.topic.out:notifications-out}")
    private String topic;

    public NotificationProducer(ObjectMapper mapper, @Autowired(required = false) @Nullable KafkaTemplate<String, String> kafkaTemplate) {
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Notification notification) {
        try {
            String payload = mapper.writeValueAsString(notification);

            if (kafkaTemplate != null) {

                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, payload);
                future.whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar notificação id={} no tópico={}", notification.getId(), topic, ex);
                    } else {
                        log.info("Notificação publicada id={} no tópico={}", notification.getId(), topic);
                    }
                });
            } else {

                log.info("KafkaTemplate não configurado — publicação simulada no tópico={} payload={}", topic, payload);
            }
        } catch (JsonProcessingException e) {
            log.error("Falha ao serializar notificação id={}", notification.getId(), e);
            throw new RuntimeException("Erro de serialização ao publicar notificação", e);
        } catch (Exception ex) {
            log.error("Erro inesperado ao publicar notificação id={}", notification.getId(), ex);
            throw ex;
        }
    }
}