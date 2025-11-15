package com.fiap.notificationservice.infrastructure.persistence;

import com.fiap.notificationservice.domain.model.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationRepositoryAdapterTest {

    @Mock
    private SpringDataNotificationRepository repo;

    @Mock
    private NotificationEntityMapper mapper;

    @InjectMocks
    private NotificationRepositoryAdapter adapter;

    @Test
    @DisplayName("Salvar deve retornar a notificação salva")
    public void save_should_return_saved_notification() {
        // Arrange
        Notification domainNotification = mock(Notification.class);
        NotificationEntity entity = mock(NotificationEntity.class);
        NotificationEntity savedEntity = mock(NotificationEntity.class);
        Notification savedDomain = mock(Notification.class);

        when(mapper.toEntity(domainNotification)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        // Act
        Notification result = adapter.save(domainNotification);

        // Assert
        assertSame(savedDomain, result);

        // Verify
        verify(mapper).toEntity(domainNotification);
        verify(repo).save(entity);
        verify(mapper).toDomain(savedEntity);
        verifyNoMoreInteractions(mapper, repo);
    }

    @Test
    @DisplayName("Buscar por id deve retornar a notificação quando existir")
    public void findById_should_return_notification_when_exists() {
        // Arrange
        UUID id = UUID.randomUUID();
        NotificationEntity entity = mock(NotificationEntity.class);
        Notification domainNotification = mock(Notification.class);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domainNotification);

        // Act
        Notification result = adapter.findById(id);

        // Assert
        assertSame(domainNotification, result);

        // Verify
        verify(repo).findById(id);
        verify(mapper).toDomain(entity);
        verifyNoMoreInteractions(mapper, repo);
    }

    @Test
    @DisplayName("Buscar por id deve retornar null quando não existir")
    public void findById_should_return_null_when_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        Notification result = adapter.findById(id);

        // Assert
        assertNull(result);

        // Verify
        verify(repo).findById(id);
        verifyNoMoreInteractions(mapper, repo);
    }
}