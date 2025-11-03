package com.fiap.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.application.usecase.ProcessParcelEventUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener que consome eventos de encomenda do tópico configurado (por padrão "parcels").
 * Recebe payload JSON compatível com ParcelEventDto e delega o processamento ao caso de uso.
 */
@Component
public class ParcelEventListener {

    private static final Logger log = LoggerFactory.getLogger(ParcelEventListener.class);

    private final ObjectMapper objectMapper;
    private final ProcessParcelEventUseCase processParcelEventUseCase;

    public ParcelEventListener(ObjectMapper objectMapper, ProcessParcelEventUseCase processParcelEventUseCase) {
        this.objectMapper = objectMapper;
        this.processParcelEventUseCase = processParcelEventUseCase;
    }

    /**
     * Escuta eventos de encomenda. Espera payload JSON compatível com ParcelEventDto.
     * O tópico pode ser configurado via propriedade parcel.topic.in (default 'parcels').
     */
    @KafkaListener(topics = "${parcel.topic.in:parcels}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void listen(String payload) {
        try {
            ParcelEventDto event = objectMapper.readValue(payload, ParcelEventDto.class);
            log.info("Evento de encomenda recebido: residentName={} apartment={} channel={}", event.residentName, event.apartment, event.channel);

            processParcelEventUseCase.execute(event);
        } catch (Exception e) {
            log.error("Falha ao processar payload do evento de encomenda={}", payload, e);
            throw new RuntimeException(e);
        }
    }
}