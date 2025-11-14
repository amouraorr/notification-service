package com.fiap.notificationservice.application.usecase;

import com.fiap.notificationservice.application.dto.ParcelEventDto;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import com.fiap.notificationservice.domain.port.NotificationSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessParcelEventUseCaseTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationSender sender;

    @InjectMocks
    private ProcessParcelEventUseCase useCase;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Test
    @DisplayName("execute - não processa quando dto é nulo")
    void shouldNotProcessWhenDtoIsNull() {
        // Arrange

        // Act
        useCase.execute(null);

        // Assert
        verifyNoInteractions(sender, repository);
    }

    @Test
    @DisplayName("execute - envia notificação quando dto válido")
    void shouldSendNotificationWhenDtoIsValid() {
        // Arrange
        ParcelEventDto dto = new ParcelEventDto();
        dto.residentName = "João";
        dto.apartment = "101";
        dto.contact = "99999-0000";
        dto.channel = "email";
        dto.receivedAt = OffsetDateTime.parse("2025-01-01T10:00:00+00:00");

        // Act
        useCase.execute(dto);

        // Assert & Verify
        verify(sender, times(1)).send(notificationCaptor.capture());
        verifyNoInteractions(repository);

        Notification sent = notificationCaptor.getValue();
        assertNotNull(sent.getId(), "id deve ser gerado");
        assertEquals("João", sent.getResidentName());
        assertEquals("101", sent.getApartment());
        assertEquals("99999-0000", sent.getContact());
        assertEquals("EMAIL", sent.getChannel(), "channel deve ser uppercase");
        assertEquals(dto.receivedAt, sent.getCreatedAt(), "createdAt deve vir do dto quando presente");
        assertFalse(sent.isAcknowledged(), "acknowledged deve ser false por default");
    }

    @Test
    @DisplayName("execute - salva fallback quando sender falha")
    void shouldSaveFallbackWhenSenderFails() {
        // Arrange
        ParcelEventDto dto = new ParcelEventDto();
        dto.residentName = "Maria";
        dto.apartment = "202";
        dto.contact = "98888-1111";
        dto.channel = null;
        dto.receivedAt = null;

        doThrow(new RuntimeException("boom")).when(sender).send(any(Notification.class));

        // Act
        useCase.execute(dto);

        // Assert & Verify
        verify(sender, times(1)).send(any(Notification.class));
        verify(repository, times(1)).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertNotNull(saved.getId(), "id do fallback deve ser gerado");
        assertEquals("Maria", saved.getResidentName());
        assertEquals("202", saved.getApartment());
        assertEquals("98888-1111", saved.getContact());
        assertEquals("PUSH", saved.getChannel(), "channel nulo deve virar PUSH no fallback");
        assertNotNull(saved.getCreatedAt(), "createdAt deve ser preenchido no fallback quando dto.receivedAt for nulo");
        assertFalse(saved.isAcknowledged(), "acknowledged deve ser false por default");
    }

    @Test
    @DisplayName("execute - não lança exceção quando sender e fallback falham")
    void shouldNotThrowWhenFallbackFails() {
        // Arrange
        ParcelEventDto dto = new ParcelEventDto();
        dto.residentName = "Carlos";
        dto.apartment = "303";
        dto.contact = "97777-2222";
        dto.channel = "push";

        doThrow(new RuntimeException("sender fail")).when(sender).send(any(Notification.class));
        doThrow(new RuntimeException("repo fail")).when(repository).save(any(Notification.class));

        // Act & Assert
        assertDoesNotThrow(() -> useCase.execute(dto));

        // Verify
        verify(sender, times(1)).send(any(Notification.class));
        verify(repository, times(1)).save(any(Notification.class));
    }
}