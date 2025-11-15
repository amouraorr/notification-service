package com.fiap.notificationservice.infrastructure.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fiap.notificationservice.domain.model.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MockProviderTest {

    @Test
    @DisplayName("Deve registrar notificação no log quando enviar")
    void send_should_logNotification() {
        // Arrange
        ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MockProvider.class);

        @SuppressWarnings("unchecked")
        Appender<ILoggingEvent> mockAppender = Mockito.mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        logger.addAppender(mockAppender);

        Notification notification = Mockito.mock(Notification.class);
        when(notification.getChannel()).thenReturn("EMAIL");
        when(notification.getContact()).thenReturn("user@example.com");
        when(notification.getMessage()).thenReturn("Hello, world!");

        MockProvider provider = new MockProvider();

        try {
            // Act
            provider.send(notification);

            // Assert
            ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
            verify(mockAppender).doAppend(captor.capture());

            ILoggingEvent loggedEvent = captor.getValue();
            String expected = "MockProvider enviando via EMAIL to user@example.com: Hello, world!";
            assertEquals(expected, loggedEvent.getFormattedMessage());

            // Verify
            verifyNoMoreInteractions(mockAppender);
        } finally {

            logger.detachAppender(mockAppender);
        }
    }
}