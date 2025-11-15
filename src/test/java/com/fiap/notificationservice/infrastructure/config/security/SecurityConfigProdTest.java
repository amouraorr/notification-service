package com.fiap.notificationservice.infrastructure.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SecurityConfigProdTest {

    @Test
    @DisplayName("Classe possui as anotações @Configuration, @Profile('prod') e @ConditionalOnClass(HttpSecurity.class)")
    public void testClassAnnotations() {
        // Arrange
        Class<?> clazz = SecurityConfigProd.class;

        // Act
        boolean hasConfiguration = clazz.isAnnotationPresent(Configuration.class);
        boolean hasProfile = clazz.isAnnotationPresent(Profile.class);
        boolean hasConditionalOnClass = clazz.isAnnotationPresent(ConditionalOnClass.class);

        // Assert
        assertTrue(hasConfiguration, "A classe deve estar anotada com @Configuration");
        assertTrue(hasProfile, "A classe deve estar anotada com @Profile");
        assertTrue(hasConditionalOnClass, "A classe deve estar anotada com @ConditionalOnClass");

        // Act
        Profile profile = clazz.getAnnotation(Profile.class);
        ConditionalOnClass conditional = clazz.getAnnotation(ConditionalOnClass.class);

        // Assert
        assertNotNull(profile, "@Profile não pode ser nulo");
        assertArrayEquals(new String[] { "prod" }, profile.value(), "O profile deve ser 'prod'");

        assertNotNull(conditional, "@ConditionalOnClass não pode ser nulo");
        Class<?>[] classes = conditional.value();
        assertTrue(Arrays.asList(classes).contains(HttpSecurity.class),
                "@ConditionalOnClass deve referenciar HttpSecurity");
    }

    @Test
    @DisplayName("Método securityFilterChain existe, é anotado com @Bean(name = 'securityFilterChainProd') e retorna SecurityFilterChain")
    public void testBeanMethodSignature() throws NoSuchMethodException {
        // Arrange
        Class<?> clazz = SecurityConfigProd.class;

        // Act
        Method method = clazz.getMethod("securityFilterChain", HttpSecurity.class);

        // Assert
        assertNotNull(method, "O método securityFilterChain deve existir");
        Bean beanAnnotation = method.getAnnotation(Bean.class);
        assertNotNull(beanAnnotation, "O método securityFilterChain deve estar anotado com @Bean");

        // Assert
        String[] names = beanAnnotation.value();
        boolean hasExpectedName = names != null && Arrays.asList(names).contains("securityFilterChainProd");
        assertTrue(hasExpectedName, "O @Bean deve expor o nome 'securityFilterChainProd'");

        // Assert
        assertEquals(SecurityFilterChain.class, method.getReturnType(), "O método deve retornar SecurityFilterChain");
    }

    @Test
    @DisplayName("securityFilterChain configura HttpSecurity e retorna o SecurityFilterChain esperado (mockado)")
    public void testSecurityFilterChain_appliesSecurityConfiguration() throws Exception {
        // Arrange
        HttpSecurity httpMock = mock(HttpSecurity.class);

        DefaultSecurityFilterChain chainMock = mock(DefaultSecurityFilterChain.class);

        when(httpMock.authorizeHttpRequests(any())).thenReturn(httpMock);
        when(httpMock.oauth2ResourceServer(any())).thenReturn(httpMock);

        when(httpMock.build()).thenReturn(chainMock);

        SecurityConfigProd config = new SecurityConfigProd();

        // Act
        SecurityFilterChain result = config.securityFilterChain(httpMock);

        // Assert
        assertSame(chainMock, result, "O SecurityFilterChain retornado deve ser o resultado do build() do HttpSecurity");

        // Verify
        verify(httpMock, times(1)).authorizeHttpRequests(any());
        verify(httpMock, times(1)).oauth2ResourceServer(any());
        verify(httpMock, times(1)).build();
    }
}