package com.fiap.notificationservice.infrastructure.provider;

import com.fiap.notificationservice.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Provider mock que "envia" notificações para e-mail/SMS/PUSH via log.
 */
@Component
public class MockProvider {
    private static final Logger log = LoggerFactory.getLogger(MockProvider.class);

    public void send(Notification notification) {
        // Em local, apenas logar. Em produção aqui seria integração com fornecedor externo.
        log.info("MockProvider enviando via {} to {}: {}", notification.getChannel(), notification.getContact(), notification.getMessage());
    }
}