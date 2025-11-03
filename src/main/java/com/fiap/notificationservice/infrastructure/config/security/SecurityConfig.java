package com.fiap.notificationservice.infrastructure.config.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * Security configuration limited to Swagger/OpenAPI endpoints.
 */
@Configuration
@ConditionalOnClass(HttpSecurity.class)
public class SecurityConfig {

    /**
     * SecurityFilterChain específico para as rotas do Swagger/OpenAPI.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(ObjectProvider<HttpSecurity> httpProvider) throws Exception {

        HttpSecurity http = httpProvider.getIfAvailable();
        if (http == null) {
            return new SecurityFilterChain() {
                @Override
                public boolean matches(HttpServletRequest request) {

                    return false;
                }

                @Override
                public List<Filter> getFilters() {

                    return Collections.emptyList();
                }
            };
        }

        http.securityMatcher(
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/swagger-resources",
                        "/webjars/**"
                )
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}