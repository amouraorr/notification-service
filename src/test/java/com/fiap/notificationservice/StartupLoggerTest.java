package com.fiap.notificationservice;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class StartupLoggerTest {

    @DisplayName("Deve registrar 'default' quando não há perfis ativos")
    @Test
    public void logsDefaultWhenNoActiveProfiles() {
        // Arrange
        Environment env = Mockito.mock(Environment.class);
        when(env.getActiveProfiles()).thenReturn(new String[]{});

        StartupLogger startupLogger = new StartupLogger(env);

        Logger logbackLogger = (Logger) LoggerFactory.getLogger(StartupLogger.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logbackLogger.addAppender(listAppender);

        // Act
        startupLogger.onReady();

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertTrue(!logsList.isEmpty(), "Nenhuma entrada de log capturada");
        String message = logsList.get(0).getFormattedMessage();
        assertTrue(message.contains("(default)"), "Mensagem de log não contém '(default)'");

        // Verify
        verify(env, times(1)).getActiveProfiles();

        // cleanup
        logbackLogger.detachAppender(listAppender);
    }

    @DisplayName("Deve registrar 'ok' quando existem perfis ativos")
    @Test
    public void logsOkWhenActiveProfiles() {
        // Arrange
        Environment env = Mockito.mock(Environment.class);
        when(env.getActiveProfiles()).thenReturn(new String[]{"dev", "prod"});

        StartupLogger startupLogger = new StartupLogger(env);

        Logger logbackLogger = (Logger) LoggerFactory.getLogger(StartupLogger.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logbackLogger.addAppender(listAppender);

        // Act
        startupLogger.onReady();

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertTrue(!logsList.isEmpty(), "Nenhuma entrada de log capturada");
        String message = logsList.get(0).getFormattedMessage();
        assertTrue(message.contains("(ok)"), "Mensagem de log não contém '(ok)'");
        assertTrue(message.contains("[dev, prod]"), "Mensagem de log não contém os perfis ativos esperados");

        // Verify
        verify(env, times(1)).getActiveProfiles();

        // cleanup
        logbackLogger.detachAppender(listAppender);
    }
}