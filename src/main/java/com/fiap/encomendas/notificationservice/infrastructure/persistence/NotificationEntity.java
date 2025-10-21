package com.fiap.encomendas.notificationservice.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "resident_name", nullable = false)
    private String residentName;

    @Column(name = "apartment")
    private String apartment;

    @Column(name = "contact")
    private String contact;

    @Column(name = "channel")
    private String channel;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "acknowledged")
    private boolean acknowledged;

    public NotificationEntity() {}

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