package com.fiap.notificationservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    @Test
    @DisplayName("Construtor com todos os argumentos inicializa todos os campos corretamente")
    void testAllArgsConstructorSetsFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Long parcelId = 123L;
        String residentName = "João Silva";
        String apartment = "Apt 101";
        String contact = "joao@example.com";
        String channel = "EMAIL";
        String message = "Sua encomenda chegou";
        String description = "Descrição ignorada";
        String status = "PENDING";
        String resultDetail = "N/A";
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime sentAt = OffsetDateTime.now();
        OffsetDateTime updatedAt = OffsetDateTime.now().plusHours(1);
        boolean acknowledged = false;

        // Act
        Notification notification = new Notification(
                id,
                parcelId,
                residentName,
                apartment,
                contact,
                channel,
                message,
                description,
                status,
                resultDetail,
                createdAt,
                sentAt,
                updatedAt,
                acknowledged
        );

        // Assert
        assertEquals(id, notification.getId());
        assertEquals(parcelId, notification.getParcelId());
        assertEquals(residentName, notification.getResidentName());
        assertEquals(apartment, notification.getApartment());
        assertEquals(contact, notification.getContact());
        assertEquals(channel, notification.getChannel());
        assertEquals(message, notification.getMessage());
        assertEquals(status, notification.getStatus());
        assertEquals(resultDetail, notification.getResultDetail());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(sentAt, notification.getSentAt());
        assertEquals(updatedAt, notification.getUpdatedAt());
        assertEquals(acknowledged, notification.isAcknowledged());
    }

    @Test
    @DisplayName("Setters e getters armazenam e retornam os valores corretos")
    void testSettersAndGetters() {
        // Arrange
        Notification notification = new Notification();

        UUID id = UUID.randomUUID();
        Long parcelId = 456L;
        String residentName = "Maria Souza";
        String apartment = "Apt 202";
        String contact = "maria@example.com";
        String channel = "SMS";
        String message = "Encomenda disponível na portaria";
        String status = "SENT";
        String resultDetail = "Delivered";
        OffsetDateTime createdAt = OffsetDateTime.now().minusHours(2);
        OffsetDateTime sentAt = OffsetDateTime.now().minusHours(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();
        boolean acknowledged = true;

        // Act
        notification.setId(id);
        notification.setParcelId(parcelId);
        notification.setResidentName(residentName);
        notification.setApartment(apartment);
        notification.setContact(contact);
        notification.setChannel(channel);
        notification.setMessage(message);
        notification.setStatus(status);
        notification.setResultDetail(resultDetail);
        notification.setCreatedAt(createdAt);
        notification.setSentAt(sentAt);
        notification.setUpdatedAt(updatedAt);
        notification.setAcknowledged(acknowledged);

        // Assert
        assertEquals(id, notification.getId());
        assertEquals(parcelId, notification.getParcelId());
        assertEquals(residentName, notification.getResidentName());
        assertEquals(apartment, notification.getApartment());
        assertEquals(contact, notification.getContact());
        assertEquals(channel, notification.getChannel());
        assertEquals(message, notification.getMessage());
        assertEquals(status, notification.getStatus());
        assertEquals(resultDetail, notification.getResultDetail());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(sentAt, notification.getSentAt());
        assertEquals(updatedAt, notification.getUpdatedAt());
        assertTrue(notification.isAcknowledged());
    }

    @Test
    @DisplayName("Alteração do flag acknowledged é registrada e pode ser verificada com Mockito.spy")
    void testAcknowledgedFlagToggleUsingSpy() {
        // Arrange
        Notification original = new Notification();
        Notification spyNotification = Mockito.spy(original);

        // Act
        spyNotification.setAcknowledged(true);
        spyNotification.setAcknowledged(false);

        // Verify
        Mockito.verify(spyNotification).setAcknowledged(true);
        Mockito.verify(spyNotification).setAcknowledged(false);

        // Assert
        assertFalse(spyNotification.isAcknowledged());
    }

    @Test
    @DisplayName("Campos de tempo (createdAt) podem ser atualizados corretamente")
    void testTimestampsMutability() {
        // Arrange
        Notification notification = new Notification();
        OffsetDateTime first = OffsetDateTime.now().minusDays(2);
        OffsetDateTime second = OffsetDateTime.now();

        // Act
        notification.setCreatedAt(first);
        assertEquals(first, notification.getCreatedAt());

        notification.setCreatedAt(second);

        // Assert
        assertEquals(second, notification.getCreatedAt());
    }
}