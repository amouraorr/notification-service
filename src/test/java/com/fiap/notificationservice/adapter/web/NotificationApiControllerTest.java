package com.fiap.notificationservice.adapter.web;

import com.fiap.notificationservice.adapter.web.mapper.NotificationWebMapper;
import com.fiap.notificationservice.application.dto.response.NotificationResponseDto;
import com.fiap.notificationservice.application.usecase.AcknowledgeNotificationUseCase;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationApiControllerTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private AcknowledgeNotificationUseCase acknowledgeUseCase;

    @Mock
    private NotificationWebMapper mapper;

    @InjectMocks
    private NotificationApiController controller;

    @Test
    @DisplayName("Retorna 200 e corpo quando notificação encontrada")
    void getById_returnsOkWhenNotificationFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        Notification notification = mock(Notification.class);
        NotificationResponseDto dto = mock(NotificationResponseDto.class);

        when(repository.findById(id)).thenReturn(notification);
        when(mapper.toDto(notification)).thenReturn(dto);

        // Act
        ResponseEntity<NotificationResponseDto> response = controller.getById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        // Verify
        verify(repository).findById(id);
        verify(mapper).toDto(notification);
    }

    @Test
    @DisplayName("Retorna 404 quando notificação não encontrada")
    void getById_returnsNotFoundWhenNotificationMissing() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(null);

        // Act
        ResponseEntity<NotificationResponseDto> response = controller.getById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(repository).findById(id);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Retorna 200 e corpo quando ack atualiza notificação")
    void acknowledge_returnsOkWhenAcknowledgeSucceeds() {
        // Arrange
        UUID id = UUID.randomUUID();
        Notification updated = mock(Notification.class);
        NotificationResponseDto dto = mock(NotificationResponseDto.class);

        when(acknowledgeUseCase.execute(id)).thenReturn(updated);
        when(mapper.toDto(updated)).thenReturn(dto);

        // Act
        ResponseEntity<NotificationResponseDto> response = controller.acknowledge(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());

        // Verify
        verify(acknowledgeUseCase).execute(id);
        verify(mapper).toDto(updated);
    }

    @Test
    @DisplayName("Retorna 404 quando ack não encontra notificação")
    void acknowledge_returnsNotFoundWhenAcknowledgeFails() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(acknowledgeUseCase.execute(id)).thenReturn(null);

        // Act
        ResponseEntity<NotificationResponseDto> response = controller.acknowledge(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(acknowledgeUseCase).execute(id);
        verify(mapper, never()).toDto(any());
    }
}