package com.fiap.notificationservice.adapter.web.mapper;

import com.fiap.notificationservice.application.dto.response.NotificationResponseDto;
import com.fiap.notificationservice.domain.model.Notification;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para conversão entre Notification (domínio) e NotificationResponseDto (API).
 */
@Mapper(componentModel = "spring")
public interface NotificationWebMapper {

    NotificationResponseDto toDto(Notification notification);
}