package com.fiap.notificationservice.domain.port;

import com.fiap.notificationservice.domain.model.Notification;

import java.util.UUID;

/**
 * Porta para persistência de Notification — implementada em infrastructure.
 */
public interface NotificationRepository {

    Notification save(Notification notification);
    Notification findById(UUID id);
}