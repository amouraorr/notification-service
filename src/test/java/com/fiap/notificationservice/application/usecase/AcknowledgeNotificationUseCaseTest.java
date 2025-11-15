package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.infrastructure.kafka.NotificationProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcknowledgeNotificationUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationProducer producer;

    @InjectMocks
    private AcknowledgeNotificationUseCase useCase;

    @Test
    @DisplayName("Retorna nulo quando a notification não existe")
    void execute_returnsNullWhenNotificationNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(null);

        // Act
        Notification result = useCase.execute(id);

        // Assert
        assertNull(result);

        // Verify
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, never()).save(any());
        verifyNoInteractions(producer);
    }

    @Test
    @DisplayName("Atualiza campos, salva e publica quando notification existe e status nulo")
    void execute_updatesFieldsSavesAndPublishesWhenNotificationExistsAndStatusNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        Notification notification = Mockito.mock(Notification.class);

        when(notificationRepository.findById(id)).thenReturn(notification);
        when(notification.isAcknowledged()).thenReturn(false);
        when(notification.getStatus()).thenReturn(null);
        when(notification.getSentAt()).thenReturn(null);
        when(notification.getId()).thenReturn(id);

        when(notificationRepository.save(notification)).thenReturn(notification);

        // Act
        Notification result = useCase.execute(id);

        // Assert
        assertEquals(notification, result);

        // Verify
        verify(notificationRepository, times(1)).findById(id);
        verify(notification, times(1)).setAcknowledged(true);
        verify(notification, times(1)).setStatus("SENT");
        verify(notification, times(1)).setSentAt(ArgumentMatchers.any(OffsetDateTime.class));
        verify(notification, times(1)).setUpdatedAt(ArgumentMatchers.any(OffsetDateTime.class));
        verify(notificationRepository, times(1)).save(notification);
        verify(producer, times(1)).publish(notification);
    }

    @Test
    @DisplayName("Trata exceção do producer e retorna o notification salvo")
    void execute_handlesProducerExceptionAndReturnsSavedNotification() {
        // Arrange
        UUID id = UUID.randomUUID();
        Notification notification = Mockito.mock(Notification.class);

        when(notificationRepository.findById(id)).thenReturn(notification);
        when(notification.isAcknowledged()).thenReturn(false);
        when(notification.getStatus()).thenReturn("SENT");
        when(notification.getId()).thenReturn(id);

        when(notificationRepository.save(notification)).thenReturn(notification);
        doThrow(new RuntimeException("Kafka down")).when(producer).publish(notification);

        // Act
        Notification result = useCase.execute(id);

        // Assert
        assertEquals(notification, result);

        // Verify
        verify(notificationRepository, times(1)).findById(id);
        verify(notification, times(1)).setAcknowledged(true);
        verify(notification, never()).setStatus(anyString());
        verify(notification, times(1)).setUpdatedAt(ArgumentMatchers.any(OffsetDateTime.class));
        verify(notificationRepository, times(1)).save(notification);
        verify(producer, times(1)).publish(notification);
    }
}