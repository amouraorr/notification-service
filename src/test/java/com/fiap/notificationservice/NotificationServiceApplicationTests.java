package com.fiap.notificationservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class NotificationServiceApplicationTests {

    @Test
    @DisplayName("Deve chamar SpringApplication.run quando main for executado com argumentos")
    void shouldInvokeSpringApplicationRun_whenMainIsCalledWithArgs() {
        // Arrange
        String[] args = new String[]{"--server.port=8080"};

        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            // Act
            NotificationServiceApplication.main(args);

            // Verify
            mocked.verify(() -> SpringApplication.run(
                    eq(NotificationServiceApplication.class),
                    eq(args)
            ), times(1));
        }
    }

    @Test
    @DisplayName("Deve chamar SpringApplication.run quando main for executado sem argumentos")
    void shouldInvokeSpringApplicationRun_whenMainIsCalledWithEmptyArgs() {
        // Arrange
        String[] args = new String[0];

        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            // Act
            NotificationServiceApplication.main(args);

            // Verify
            mocked.verify(() -> SpringApplication.run(
                    eq(NotificationServiceApplication.class),
                    eq(args)
            ), times(1));
        }
    }
}