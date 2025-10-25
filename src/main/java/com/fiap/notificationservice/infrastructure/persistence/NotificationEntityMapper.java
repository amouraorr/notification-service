package com.fiap.notificationservice.infrastructure.persistence;

import com.fiap.notificationservice.domain.model.Notification;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct que converte entre Notification (domínio) e NotificationEntity (persistência).
 */
@Mapper(componentModel = "spring")
public interface NotificationEntityMapper {

    NotificationEntity toEntity(Notification n);

    Notification toDomain(NotificationEntity e);
}