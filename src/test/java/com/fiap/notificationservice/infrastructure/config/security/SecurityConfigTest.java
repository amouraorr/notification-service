package com.fiap.notificationservice.infrastructure.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private ObjectProvider<HttpSecurity> httpProvider;

    @Mock
    private HttpSecurity http;

    @Mock
    private DefaultSecurityFilterChain chain;

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("Deve retornar um SecurityFilterChain vazio quando HttpSecurity não estiver disponível")
    void shouldReturnEmptyChainWhenHttpMissing() throws Exception {
        // Arrange
        when(httpProvider.getIfAvailable()).thenReturn(null);

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpProvider);

        // Assert
        assertNotNull(result);
        assertFalse(result.matches(null));
        assertTrue(result.getFilters().isEmpty());
    }

    @Test
    @DisplayName("Deve configurar HttpSecurity e retornar o SecurityFilterChain construído quando HttpSecurity estiver disponível")
    void shouldConfigureAndBuildChainWhenHttpPresent() throws Exception {
        // Arrange
        when(httpProvider.getIfAvailable()).thenReturn(http);

        doReturn(http).when(http).securityMatcher(any(String[].class));
        doReturn(http).when(http).authorizeHttpRequests(any());
        doReturn(http).when(http).csrf(any());

        doReturn(chain).when(http).build();

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpProvider);

        // Assert
        assertNotNull(result);
        assertSame(chain, result);

        // Verify
        verify(http).securityMatcher(any(String[].class));
        verify(http).authorizeHttpRequests(any());
        verify(http).csrf(any());
        verify(http).build();
    }
}