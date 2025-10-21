package com.fiap.encomendas.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.encomendas.notificationservice.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer que publica Notification para tópico de saída.
 */
@Component
public class NotificationProducer {
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String topicOut;

    public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate, org.springframework.core.env.Environment env) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicOut = env.getProperty("app.kafka.topic.notifications-out", "notifications-out");
    }

    public void publish(Notification notification) {
        try {
            String payload = mapper.writeValueAsString(notification);
            kafkaTemplate.send(topicOut, notification.getId().toString(), payload);
            log.info("Published notification to topic={} id={}", topicOut, notification.getId());
        } catch (Exception e) {
            log.error("Failed to publish notification", e);
        }
    }
}