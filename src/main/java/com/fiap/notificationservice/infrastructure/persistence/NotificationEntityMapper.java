package com.fiap.notificationservice.infrastructure.persistence;

import com.fiap.notificationservice.domain.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper manual que converte entre Notification (domínio) e NotificationEntity (persistência).
 */
@Component
public class NotificationEntityMapper {

    public NotificationEntity toEntity(Notification n) {
        if (n == null) return null;
        NotificationEntity e = new NotificationEntity();
        e.setId(n.getId());
        e.setParcelId(n.getParcelId());
        e.setResidentName(n.getResidentName());
        e.setApartment(n.getApartment());
        e.setContact(n.getContact());
        e.setChannel(n.getChannel());
        e.setMessage(n.getMessage());
        e.setStatus(n.getStatus());
        e.setResultDetail(n.getResultDetail());
        e.setCreatedAt(n.getCreatedAt());
        e.setSentAt(n.getSentAt());
        e.setUpdatedAt(n.getUpdatedAt());
        e.setAcknowledged(n.isAcknowledged());
        return e;
    }

    public Notification toDomain(NotificationEntity e) {
        if (e == null) return null;
        Notification n = new Notification();
        n.setId(e.getId());
        n.setParcelId(e.getParcelId());
        n.setResidentName(e.getResidentName());
        n.setApartment(e.getApartment());
        n.setContact(e.getContact());
        n.setChannel(e.getChannel());
        n.setMessage(e.getMessage());
        n.setStatus(e.getStatus());
        n.setResultDetail(e.getResultDetail());
        n.setCreatedAt(e.getCreatedAt());
        n.setSentAt(e.getSentAt());
        n.setUpdatedAt(e.getUpdatedAt());
        n.setAcknowledged(e.isAcknowledged());
        return n;
    }
}