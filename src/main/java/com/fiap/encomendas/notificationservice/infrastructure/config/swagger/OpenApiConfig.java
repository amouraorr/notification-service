package com.fiap.encomendas.notificationservice.infrastructure.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE NOTIFICAÇÕES")
                        .version("1.0.0")
                        .description("Microsserviço responsável pelo envio de notificações aos moradores (e-mail, SMS, push), persistência do histórico de notificações e integração com provedores externos via Kafka e adaptadores de envio."));
    }
}