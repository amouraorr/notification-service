package com.fiap.notificationservice.infrastructure.config.jacksonconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {

    @Test
    @DisplayName("Deve registrar JavaTimeModule e desabilitar WRITE_DATES_AS_TIMESTAMPS")
    void objectMapper_registersJavaTimeModuleAndDisablesWriteDatesAsTimestamps() {
        // Arrange
        JacksonConfig config = new JacksonConfig();

        // Act
        ObjectMapper mapper = config.objectMapper();

        // Assert
        assertNotNull(mapper);
        assertFalse(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertTrue(mapper.canSerialize(OffsetDateTime.class));
        assertTrue(mapper.canDeserialize(mapper.constructType(OffsetDateTime.class)));
    }
}