package com.fiap.notificationservice.infrastructure.persistence;

import com.fiap.notificationservice.domain.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper manual que converte entre Notification (domínio) e NotificationEntity (persistência).
 */
@Component
public class NotificationEntityMapper {

    /**
     * Converte domínio -> entidade JPA.
     */
    public NotificationEntity toEntity(Notification n) {
        if (n == null) return null;
        NotificationEntity e = new NotificationEntity();
        e.setId(n.getId());
        e.setResidentName(n.getResidentName());
        e.setApartment(n.getApartment());
        e.setContact(n.getContact());
        e.setChannel(n.getChannel());
        e.setMessage(n.getMessage());
        e.setCreatedAt(n.getCreatedAt());
        e.setSentAt(n.getSentAt());
        e.setAcknowledged(n.isAcknowledged());
        return e;
    }

    /**
     * Converte entidade JPA -> domínio.
     */
    public Notification toDomain(NotificationEntity e) {
        if (e == null) return null;
        Notification n = new Notification();
        n.setId(e.getId());
        n.setResidentName(e.getResidentName());
        n.setApartment(e.getApartment());
        n.setContact(e.getContact());
        n.setChannel(e.getChannel());
        n.setMessage(e.getMessage());
        n.setCreatedAt(e.getCreatedAt());
        n.setSentAt(e.getSentAt());
        n.setAcknowledged(e.isAcknowledged());
        return n;
    }
}