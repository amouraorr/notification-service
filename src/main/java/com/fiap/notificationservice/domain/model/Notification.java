package com.fiap.notificationservice.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade de domínio Notification.
 */
public class Notification {

    private UUID id;
    private Long parcelId;
    private String residentName;
    private String apartment;
    private String contact; // telefone/email
    private String channel; // EMAIL,SMS,PUSH
    private String message;
    private String description;
    private String status; // PENDING, SENT, FAILED
    private String resultDetail;
    private OffsetDateTime createdAt;
    private OffsetDateTime sentAt;
    private OffsetDateTime updatedAt;
    private boolean acknowledged;

    public Notification(UUID id,
                        Long parcelId,
                        String residentName,
                        String apartment,
                        String contact,
                        String channel,
                        String message,
                        String description,
                        String status,
                        String resultDetail,
                        OffsetDateTime createdAt,
                        OffsetDateTime sentAt,
                        OffsetDateTime updatedAt,
                        boolean acknowledged) {
        this.id = id;
        this.parcelId = parcelId;
        this.residentName = residentName;
        this.apartment = apartment;
        this.contact = contact;
        this.channel = channel;
        this.message = message;
        this.description = description;
        this.status = status;
        this.resultDetail = resultDetail;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.updatedAt = updatedAt;
        this.acknowledged = acknowledged;
    }

    public Notification() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getParcelId() { return parcelId; }
    public void setParcelId(Long parcelId) { this.parcelId = parcelId; }

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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResultDetail() { return resultDetail; }
    public void setResultDetail(String resultDetail) { this.resultDetail = resultDetail; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
}