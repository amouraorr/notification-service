package com.fiap.notificationservice.infrastructure.provider;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.infrastructure.kafka.NotificationProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSenderAdapterTest {

    @Mock
    private MockProvider mockProvider;

    @Mock
    private NotificationProducer producer;

    @Mock
    private NotificationRepository repository;

    @Mock
    private Notification notification;

    @InjectMocks
    private NotificationSenderAdapter adapter;

    @Test
    @DisplayName("Enviar notificação: deve salvar e publicar quando tudo ocorrer bem")
    void send_shouldSetSentAtAndSaveAndPublish_whenAllOk() {
        // Arrange
        ArgumentCaptor<OffsetDateTime> sentAtCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        // Act
        adapter.send(notification);

        // Verify
        verify(notification).setSentAt(sentAtCaptor.capture());

        // Assert
        assertNotNull(sentAtCaptor.getValue());

        // Verify
        verify(repository).save(notification);

        // Verify
        verify(producer).publish(notification);
    }

    @Test
    @DisplayName("Enviar notificação: deve continuar quando MockProvider lançar exceção")
    void send_shouldContinue_whenMockProviderThrows() {
        // Arrange
        doThrow(new RuntimeException("provider fail")).when(mockProvider).send(notification);
        ArgumentCaptor<OffsetDateTime> sentAtCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        // Act
        adapter.send(notification);

        // Verify
        verify(notification).setSentAt(sentAtCaptor.capture());

        // Act
        assertNotNull(sentAtCaptor.getValue());

        // Verify
        verify(repository).save(notification);

        // Verify
        verify(producer).publish(notification);
    }

    @Test
    @DisplayName("Enviar notificação: deve continuar e publicar quando salvar no repository lançar exceção")
    void send_shouldContinue_whenRepositoryThrows() {
        // Arrange
        doThrow(new RuntimeException("db fail")).when(repository).save(notification);
        ArgumentCaptor<OffsetDateTime> sentAtCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        // Act
        adapter.send(notification);

        // Verify
        verify(notification).setSentAt(sentAtCaptor.capture());

        // Assert
        assertNotNull(sentAtCaptor.getValue());

        // Verify
        verify(repository).save(notification);

        // Verify
        verify(producer).publish(notification);
    }

    @Test
    @DisplayName("Enviar notificação: deve continuar e salvar quando producer lançar exceção")
    void send_shouldContinue_whenProducerThrows() {
        // Arrange
        doThrow(new RuntimeException("kafka fail")).when(producer).publish(notification);
        ArgumentCaptor<OffsetDateTime> sentAtCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        // Act
        adapter.send(notification);

        // Verify
        verify(notification).setSentAt(sentAtCaptor.capture());

        // Assert
        assertNotNull(sentAtCaptor.getValue());

        // Verify
        verify(repository).save(notification);

        // Verify
        verify(producer).publish(notification);
    }
}