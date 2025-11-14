package com.fiap.notificationservice.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private org.springframework.core.env.Environment env;

    private ObjectMapper objectMapper;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        when(env.getProperty(eq("EXTERNAL_PROVIDERS_MOCK"), anyString())).thenReturn("http://mock-providers");
        service = new NotificationService(repository, objectMapper, kafkaTemplate, env);
    }

    @Test
    @DisplayName("Deve enviar push quando channel não for informado")
    void should_send_push_when_no_channel_specified() {
        // Arrange
        Map<String, Object> event = Map.of(
                "parcelId", 123L,
                "residentName", "João",
                "apartment", "101",
                "description", "Pacote pequeno"
        );
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        // Act
        service.handleParcelEvent(event);
        // Assert & Verify
        verify(repository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("PUSH", saved.getChannel());
        assertEquals("SENT", saved.getStatus());
        assertEquals("push:ok", saved.getResultDetail());
        assertNotNull(saved.getSentAt());
        // Verify
        verify(kafkaTemplate, times(1)).send(eq("notifications.sent"), eq(String.valueOf(123L)), anyString());
    }

    @Test
    @DisplayName("Deve persistir PENDING quando SMS e sem contact")
    void should_persist_pending_when_sms_and_no_contact() {
        // Arrange
        Map<String, Object> event = Map.of(
                "parcelId", 222L,
                "channel", "SMS",
                "contact", "",
                "residentName", "Maria",
                "apartment", "202"
        );
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        // Act
        service.handleParcelEvent(event);
        // Assert & Verify
        verify(repository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("PENDING", saved.getStatus());
        assertEquals("no-contact", saved.getResultDetail());
        // Verify
        verify(kafkaTemplate, times(1)).send(eq("notifications.sent"), eq(String.valueOf(222L)), anyString());
    }

    @Test
    @DisplayName("Deve usar receivedAt quando fornecido como OffsetDateTime")
    void should_use_receivedAt_when_present() {
        // Arrange
        OffsetDateTime dt = OffsetDateTime.now().minusDays(1);
        Map<String, Object> event = Map.of(
                "parcelId", 555L,
                "residentName", "Paulo",
                "apartment", "505",
                "receivedAt", dt
        );
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        // Act
        service.handleParcelEvent(event);
        // Assert & Verify
        verify(repository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals(dt, saved.getCreatedAt());
        verify(kafkaTemplate, times(1)).send(eq("notifications.sent"), eq(String.valueOf(555L)), anyString());
    }
}