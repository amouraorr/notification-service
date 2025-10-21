package com.fiap.encomendas.notificationservice.domain.port;

import com.fiap.encomendas.notificationservice.domain.model.Notification;

/**
 * Porta para enviar notificações para provedores (email/sms/push).
 */
public interface NotificationSender {

    void send(Notification notification);
}