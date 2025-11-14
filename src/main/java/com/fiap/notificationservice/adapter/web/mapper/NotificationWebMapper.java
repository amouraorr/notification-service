package com.fiap.notificationservice.adapter.web.mapper;

import com.fiap.notificationservice.application.dto.response.NotificationResponseDto;
import com.fiap.notificationservice.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationWebMapper {

    /**
     * Converte a entidade de domínio para o DTO de resposta da API.
     */
    public NotificationResponseDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationResponseDto.from(notification);
    }
}