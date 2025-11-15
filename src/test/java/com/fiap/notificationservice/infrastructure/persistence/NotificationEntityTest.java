package com.fiap.notificationservice.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationEntityTest {

    @Test
    @DisplayName("prePersist deve definir id, createdAt, channel, status e updatedAt quando estiverem nulos")
    void prePersist_setsDefaultsWhenNull() {
        // Arrange
        NotificationEntity entity = new NotificationEntity();

        assertNull(entity.getId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getChannel());
        assertNull(entity.getStatus());
        assertNull(entity.getUpdatedAt());

        // Act
        entity.prePersist();

        // Assert
        assertNotNull(entity.getId(), "id deve ser definido");
        assertNotNull(entity.getCreatedAt(), "createdAt deve ser definido");
        assertNotNull(entity.getUpdatedAt(), "updatedAt deve ser definido");
        assertEquals("PUSH", entity.getChannel(), "channel default deve ser PUSH");
        assertEquals("PENDING", entity.getStatus(), "status default deve ser PENDING");
        assertEquals(entity.getCreatedAt(), entity.getUpdatedAt(), "updatedAt deve ser igual a createdAt após prePersist");
    }

    @Test
    @DisplayName("prePersist não deve sobrescrever id, createdAt, channel ou status quando já definidos")
    void prePersist_doesNotOverrideExistingValues() {
        // Arrange
        NotificationEntity entity = new NotificationEntity();
        UUID existingId = UUID.randomUUID();
        OffsetDateTime existingCreatedAt = OffsetDateTime.now().minusDays(1);
        String existingChannel = "SMS";
        String existingStatus = "SENT";

        entity.setId(existingId);
        entity.setCreatedAt(existingCreatedAt);
        entity.setChannel(existingChannel);
        entity.setStatus(existingStatus);

        // Act
        entity.prePersist();

        // Assert
        assertEquals(existingId, entity.getId(), "id não deve ser sobrescrito");
        assertEquals(existingCreatedAt, entity.getCreatedAt(), "createdAt não deve ser sobrescrito");
        assertEquals(existingChannel, entity.getChannel(), "channel não deve ser sobrescrito");
        assertEquals(existingStatus, entity.getStatus(), "status não deve ser sobrescrito");
        assertEquals(existingCreatedAt, entity.getUpdatedAt(), "updatedAt deve ser igual ao createdAt existente após prePersist");
    }

    @Test
    @DisplayName("preUpdate deve atualizar updatedAt para um valor mais recente")
    void preUpdate_updatesUpdatedAt() {
        // Arrange
        NotificationEntity entity = new NotificationEntity();
        OffsetDateTime oldUpdatedAt = OffsetDateTime.now().minusHours(1);
        entity.setUpdatedAt(oldUpdatedAt);

        // Act
        entity.preUpdate();

        // Assert
        assertNotNull(entity.getUpdatedAt(), "updatedAt não deve ser nulo após preUpdate");
        assertTrue(entity.getUpdatedAt().isAfter(oldUpdatedAt), "updatedAt deve ser posterior ao valor anterior");
    }

    @Test
    @DisplayName("Getters e Setters devem armazenar e recuperar valores corretamente")
    void gettersAndSetters_workProperly() {
        // Arrange
        NotificationEntity entity = new NotificationEntity();
        UUID id = UUID.randomUUID();
        Long parcelId = 123L;
        String residentName = "Fulano";
        String apartment = "101A";
        String contact = "999999999";
        String channel = "EMAIL";
        String message = "Mensagem teste";
        String status = "DELIVERED";
        String resultDetail = "Detalhe do resultado";
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(2);
        OffsetDateTime sentAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();
        boolean acknowledged = true;

        // Act
        entity.setId(id);
        entity.setParcelId(parcelId);
        entity.setResidentName(residentName);
        entity.setApartment(apartment);
        entity.setContact(contact);
        entity.setChannel(channel);
        entity.setMessage(message);
        entity.setStatus(status);
        entity.setResultDetail(resultDetail);
        entity.setCreatedAt(createdAt);
        entity.setSentAt(sentAt);
        entity.setUpdatedAt(updatedAt);
        entity.setAcknowledged(acknowledged);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals(parcelId, entity.getParcelId());
        assertEquals(residentName, entity.getResidentName());
        assertEquals(apartment, entity.getApartment());
        assertEquals(contact, entity.getContact());
        assertEquals(channel, entity.getChannel());
        assertEquals(message, entity.getMessage());
        assertEquals(status, entity.getStatus());
        assertEquals(resultDetail, entity.getResultDetail());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(sentAt, entity.getSentAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
        assertTrue(entity.isAcknowledged());
    }
}