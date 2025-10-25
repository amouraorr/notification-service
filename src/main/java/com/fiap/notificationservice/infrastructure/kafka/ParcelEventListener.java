package com.fiap.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.application.usecase.ProcessParcelEventUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener que consome eventos de encomenda do tópico "parcels-in".
 */
@Component
public class ParcelEventListener {
    private static final Logger log = LoggerFactory.getLogger(ParcelEventListener.class);

    private final ProcessParcelEventUseCase useCase;
    private final ObjectMapper mapper = new ObjectMapper();

    public ParcelEventListener(ProcessParcelEventUseCase useCase) {
        this.useCase = useCase;
    }

    @KafkaListener(topics = "${app.kafka.topic.parcels-in:parcels-in}", groupId = "${spring.kafka.consumer.group-id:notification-service-group}")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            log.info("Received parcel event: key={}", record.key());
            ParcelEventDto dto = mapper.readValue(record.value(), ParcelEventDto.class);
            useCase.execute(dto);
        } catch (Exception ex) {
            log.error("Failed to process parcel event", ex);
        }
    }
}