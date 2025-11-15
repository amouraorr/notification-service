package com.fiap.notificationservice.infrastructure.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    @DisplayName("Deve criar OpenAPI com informações corretas ao instanciar a configuração diretamente")
    void objectCreation_shouldProvideOpenAPIWithCorrectInfo() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI, "OpenAPI não deve ser nulo");
        Info info = openAPI.getInfo();
        assertNotNull(info, "Info não deve ser nulo");
        assertEquals("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE NOTIFICAÇÕES", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertTrue(info.getDescription().contains("Microsserviço responsável pelo envio de notificações"), "Descrição deve conter texto esperado");
    }

    @Test
    @DisplayName("Deve registrar o bean OpenAPI no contexto Spring")
    void context_shouldRegisterOpenAPIBean() {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(OpenApiConfig.class);
        ctx.refresh();

        try {
            // Act
            OpenAPI openAPI = ctx.getBean(OpenAPI.class);

            // Assert
            assertNotNull(openAPI, "Bean OpenAPI deve estar presente no contexto");
            Info info = openAPI.getInfo();
            assertNotNull(info, "Info do OpenAPI não deve ser nulo");
            assertEquals("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE NOTIFICAÇÕES", info.getTitle());
            assertEquals("1.0.0", info.getVersion());
            // Verify
            assertTrue(info.getDescription().contains("persistência do histórico de notificações"), "Descrição deve conter trecho sobre persistência do histórico");
        } finally {
            ctx.close();
        }
    }
}