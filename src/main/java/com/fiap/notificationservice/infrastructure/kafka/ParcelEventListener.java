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
 * Kafka listener que consome eventos de encomenda do tópico configurado (por padrão "parcels-in").
 */
@Component
public class ParcelEventListener {
    private static final Logger log = LoggerFactory.getLogger(ParcelEventListener.class);

    private final ProcessParcelEventUseCase useCase;
    private final ObjectMapper mapper;

    public ParcelEventListener(ProcessParcelEventUseCase useCase, ObjectMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "${kafka.topics.parcels-in:parcels-in}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record) {
        try {
            log.info("Recebido evento de encomenda: tópico={} partição={} offset={} chave={}",
                    record.topic(), record.partition(), record.offset(), record.key());
            String preview = preview(record.value());
            log.debug("Visualização do evento de encomenda: {}", preview);

            ParcelEventDto dto = mapper.readValue(record.value(), ParcelEventDto.class);
            useCase.execute(dto);
        } catch (Exception ex) {
            log.error("Falha ao processar evento de encomenda (tópico={}, partição={}, offset={}, chave={})",
                    record.topic(), record.partition(), record.offset(), record.key(), ex);
        }
    }

    private String preview(String s) {
        if (s == null) return "";
        int max = 800;
        return s.length() <= max ? s : s.substring(0, max) + "...[truncado]";
    }
}