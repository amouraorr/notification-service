package com.fiap.encomendas.notificationservice.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade de domínio Notification.
 */
public class Notification {
    private UUID id;
    private String residentName;
    private String apartment;
    private String contact; // telefone/email
    private String channel; // EMAIL,SMS,PUSH
    private String message;
    private OffsetDateTime createdAt;
    private OffsetDateTime sentAt;
    private boolean acknowledged;

    public Notification(UUID id, String residentName, String apartment, String contact, String channel, String message, OffsetDateTime createdAt, OffsetDateTime sentAt, boolean acknowledged) {

        this.id = id;
        this.residentName = residentName;
        this.apartment = apartment;
        this.contact = contact;
        this.channel = channel;
        this.message = message;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.acknowledged = acknowledged;
    }

    public Notification() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getApartment() { return apartment; }
    public void setApartment(String apartment) { this.apartment = apartment; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
}