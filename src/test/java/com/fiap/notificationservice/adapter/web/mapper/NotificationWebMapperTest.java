package com.fiap.notificationservice.adapter.web.mapper;

import com.fiap.notificationservice.application.dto.response.NotificationResponseDto;
import com.fiap.notificationservice.domain.model.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationWebMapperTest {

    private final NotificationWebMapper mapper = new NotificationWebMapper();

    @Test
    @DisplayName("Retorna nulo quando a notificação é nula")
    void toDto_returnsNullWhenNotificationIsNull() {
        // Arrange

        // Act
        NotificationResponseDto result = mapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Chama NotificationResponseDto.from e retorna DTO quando notificação não é nula")
    void toDto_callsFromAndReturnsDtoWhenNotificationIsNotNull() {
        // Arrange
        Notification mockNotification = Mockito.mock(Notification.class);
        NotificationResponseDto mockedDto = Mockito.mock(NotificationResponseDto.class);

        try (MockedStatic<NotificationResponseDto> mockedStatic = Mockito.mockStatic(NotificationResponseDto.class)) {
            mockedStatic.when(() -> NotificationResponseDto.from(mockNotification)).thenReturn(mockedDto);

            // Act
            NotificationResponseDto result = mapper.toDto(mockNotification);

            // Assert
            assertEquals(mockedDto, result);

            // Verify
            mockedStatic.verify(() -> NotificationResponseDto.from(mockNotification), Mockito.times(1));
        }
    }
}