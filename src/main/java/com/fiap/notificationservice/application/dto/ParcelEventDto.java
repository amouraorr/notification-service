package com.fiap.notificationservice.application.dto;

import java.time.OffsetDateTime;

/**
 * DTO do evento recebido do Parcel Service.
 */
public class ParcelEventDto {

    public String residentName;
    public String apartment;
    public String contact; // telefone ou email
    public String channel; // EMAIL, SMS, PUSH
    public String description;
    public OffsetDateTime receivedAt;

    public ParcelEventDto() {}
}