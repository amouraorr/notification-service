package com.fiap.notificationservice.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de resposta simples para a API.
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
}