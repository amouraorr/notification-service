package com.fiap.notificationservice.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.domain.model.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("Deve publicar notificação quando KafkaTemplate estiver configurado")
    void publish_shouldCallKafkaTemplateSend_whenKafkaTemplatePresent() throws Exception {
        // Arrange
        ObjectMapper mapper = spy(new ObjectMapper());

        NotificationProducer producer = new NotificationProducer(mapper, kafkaTemplate);

        Notification notification = mock(Notification.class);

        doReturn(null).when(notification).getId();

        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(
                isNull(),
                isNull(),
                anyString()
        )).thenReturn(future);

        // Act & Assert
        assertDoesNotThrow(() -> producer.publish(notification));

        // Verify
        verify(kafkaTemplate, times(1)).send(
                isNull(),
                isNull(),
                anyString()
        );
    }

    @Test
    @DisplayName("Deve simular publicação quando KafkaTemplate não estiver configurado")
    void publish_shouldSimulateWhenKafkaTemplateIsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = spy(new ObjectMapper());

        NotificationProducer producer = new NotificationProducer(mapper, null);

        Notification notification = mock(Notification.class);
        doReturn(null).when(notification).getId();

        // Act & Assert
        assertDoesNotThrow(() -> producer.publish(notification));

        // Verify
        verify(mapper, atLeastOnce()).writeValueAsString(notification);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando ocorrer erro de serialização")
    void publish_shouldThrowRuntimeException_whenSerializationFails() throws Exception {
        // Arrange
        ObjectMapper mapper = mock(ObjectMapper.class);
        Notification notification = mock(Notification.class);
        doReturn(null).when(notification).getId();

        when(mapper.writeValueAsString(notification)).thenThrow(new JsonProcessingException("erro") {});

        NotificationProducer producer = new NotificationProducer(mapper, kafkaTemplate);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> producer.publish(notification));

        // Verify
        verify(kafkaTemplate, never()).send(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Não deve propagar exceção quando future de send for completado excepcionalmente")
    void publish_shouldNotPropagate_whenSendFutureCompletedExceptionally() throws Exception {
        // Arrange
        ObjectMapper mapper = spy(new ObjectMapper());
        NotificationProducer producer = new NotificationProducer(mapper, kafkaTemplate);

        Notification notification = mock(Notification.class);
        doReturn(null).when(notification).getId();

        CompletableFuture<SendResult<String, String>> failedFuture =
                CompletableFuture.failedFuture(new RuntimeException("kafka error"));

        when(kafkaTemplate.send(
                isNull(),
                isNull(),
                anyString()
        )).thenReturn(failedFuture);

        // Act & Assert
        assertDoesNotThrow(() -> producer.publish(notification));

        // Verify
        verify(kafkaTemplate, times(1)).send(
                isNull(),
                isNull(),
                anyString()
        );
    }
}