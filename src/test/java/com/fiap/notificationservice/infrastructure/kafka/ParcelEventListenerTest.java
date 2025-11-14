package com.fiap.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.application.usecase.ProcessParcelEventUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ParcelEventListenerTest {

    @Test
    @DisplayName("Deve processar evento válido e chamar useCase.execute")
    void shouldProcessValidEvent() throws Exception {
        // Arrange
        ProcessParcelEventUseCase useCase = mock(ProcessParcelEventUseCase.class);
        ObjectMapper mapper = mock(ObjectMapper.class);
        ParcelEventListener listener = new ParcelEventListener(useCase, mapper);

        String topic = "parcels-in";
        int partition = 0;
        long offset = 1L;
        String key = "my-key";
        String json = "{\"id\":\"123\"}";

        ParcelEventDto dto = mock(ParcelEventDto.class);
        when(mapper.readValue(eq(json), eq(ParcelEventDto.class))).thenReturn(dto);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, partition, offset, key, json);

        // Act
        listener.listen(record);

        // Assert & Verify
        verify(useCase, times(1)).execute(eq(dto));
    }

    @Test
    @DisplayName("Deve tratar exceção do mapper sem lançar e sem chamar useCase")
    void shouldHandleMapperExceptionGracefully() throws Exception {
        // Arrange
        ProcessParcelEventUseCase useCase = mock(ProcessParcelEventUseCase.class);
        ObjectMapper mapper = mock(ObjectMapper.class);
        ParcelEventListener listener = new ParcelEventListener(useCase, mapper);

        String topic = "parcels-in";
        int partition = 1;
        long offset = 42L;
        String key = "another-key";
        String json = "{\"invalid\":}";

        when(mapper.readValue(eq(json), eq(ParcelEventDto.class))).thenThrow(new RuntimeException("json parse error"));

        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, partition, offset, key, json);

        // Act & Assert
        assertDoesNotThrow(() -> listener.listen(record));

        // Verify
        verify(useCase, never()).execute(any());
    }
}