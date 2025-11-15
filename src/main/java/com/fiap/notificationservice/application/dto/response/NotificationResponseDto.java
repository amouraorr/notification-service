package com.fiap.notificationservice.application.dto.response;

import com.fiap.notificationservice.domain.model.Notification;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO usado para exposição/serialização de Notification.
 */
public class NotificationResponseDto {
    public UUID id;
    public String residentName;
    public String apartment;
    public String contact;
    public String channel;
    public String message;
    public OffsetDateTime createdAt;
    public OffsetDateTime sentAt;
    public boolean acknowledged;

    public NotificationResponseDto() {}

    public static NotificationResponseDto from(Notification n) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.id = n.getId();
        dto.residentName = n.getResidentName();
        dto.apartment = n.getApartment();
        dto.contact = n.getContact();
        dto.channel = n.getChannel();
        dto.message = n.getMessage();
        dto.createdAt = n.getCreatedAt();
        dto.sentAt = n.getSentAt();
        dto.acknowledged = n.isAcknowledged();
        return dto;
    }
}