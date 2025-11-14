package com.fiap.notificationservice.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.notificationservice.infrastructure.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationKafkaListenerTest {

    private ObjectMapper objectMapper;
    private NotificationService notificationService;
    private NotificationKafkaListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        notificationService = mock(NotificationService.class);
        listener = new NotificationKafkaListener(objectMapper, notificationService);
    }

    @Test
    @DisplayName("Deve processar payload válido e delegar ao NotificationService")
    public void testOnMessage_validPayload_callsNotificationService() throws Exception {
        // Arrange
        String payload = "{\"id\":\"123\",\"status\":\"DELIVERED\"}";
        Map<String, Object> evt = new HashMap<>();
        evt.put("id", "123");
        evt.put("status", "DELIVERED");
        when(objectMapper.readValue(payload, Map.class)).thenReturn(evt);

        // Act
        listener.onMessage(payload);

        // Assert & Verify
        verify(notificationService, times(1)).handleParcelEvent(evt);
    }

    @Test
    @DisplayName("Deve tratar exceção ao desserializar payload sem propagar e não chamar NotificationService")
    public void testOnMessage_invalidPayload_doesNotCallServiceAndDoesNotThrow() throws Exception {
        // Arrange
        String payload = "invalid json";
        when(objectMapper.readValue(payload, Map.class)).thenThrow(new RuntimeException("JSON parse error"));

        // Act
        listener.onMessage(payload);

        // Assert & Verify
        verify(notificationService, never()).handleParcelEvent(any());
    }
}