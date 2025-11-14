package com.fiap.notificationservice.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.mock.env.MockEnvironment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class KafkaConfigTest {

    private final KafkaConfig config = new KafkaConfig();

    @Test
    @DisplayName("kafkaListenerContainerFactory deve configurar o ConsumerFactory fornecido")
    void kafkaListenerContainerFactory_should_set_consumerFactory() {
        // Arrange
        ConsumerFactory<String, String> consumerFactory = mock(ConsumerFactory.class);

        // Act
        ConcurrentKafkaListenerContainerFactory<String, String> factory = config.kafkaListenerContainerFactory(consumerFactory);

        // Assert
        assertSame(consumerFactory, factory.getConsumerFactory());
    }

    @Test
    @DisplayName("consumerFactory deve usar propriedades do Environment quando fornecidas")
    void consumerFactory_should_respect_environment_properties() {
        // Arrange
        MockEnvironment env = new MockEnvironment();
        env.setProperty("SPRING_KAFKA_BOOTSTRAP_SERVERS", "broker:1234");
        env.setProperty("KAFKA_CONSUMER_GROUP_ID", "group-x");

        // Act
        ConsumerFactory<String, String> cf = config.consumerFactory(env);

        // Assert
        assertNotNull(cf);
        Map<String, Object> props = ((DefaultKafkaConsumerFactory<String, String>) cf).getConfigurationProperties();
        assertEquals("broker:1234", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("group-x", props.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("earliest", props.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        assertEquals(org.apache.kafka.common.serialization.StringDeserializer.class, props.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(org.apache.kafka.common.serialization.StringDeserializer.class, props.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
    }

    @Test
    @DisplayName("consumerFactory deve usar valores padrão quando Environment não fornecer propriedades")
    void consumerFactory_should_use_defaults_when_env_missing() {
        // Arrange
        MockEnvironment env = new MockEnvironment();

        // Act
        ConsumerFactory<String, String> cf = config.consumerFactory(env);

        // Assert
        assertNotNull(cf);
        Map<String, Object> props = ((DefaultKafkaConsumerFactory<String, String>) cf).getConfigurationProperties();
        assertEquals("kafka:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("notification-service", props.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("earliest", props.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
    }

    @Test
    @DisplayName("producerFactory deve usar propriedade SPRING_KAFKA_BOOTSTRAP_SERVERS quando fornecida")
    void producerFactory_should_respect_environment_properties() {
        // Arrange
        MockEnvironment env = new MockEnvironment();
        env.setProperty("SPRING_KAFKA_BOOTSTRAP_SERVERS", "prodbroker:9093");

        // Act
        ProducerFactory<String, String> pf = config.producerFactory(env);

        // Assert
        assertNotNull(pf);
        Map<String, Object> props = ((DefaultKafkaProducerFactory<String, String>) pf).getConfigurationProperties();
        assertEquals("prodbroker:9093", props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(org.apache.kafka.common.serialization.StringSerializer.class, props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(org.apache.kafka.common.serialization.StringSerializer.class, props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    @DisplayName("kafkaTemplate deve usar o ProducerFactory fornecido")
    void kafkaTemplate_should_use_producerFactory() {
        // Arrange
        ProducerFactory<String, String> pf = mock(ProducerFactory.class);

        // Act
        KafkaTemplate<String, String> template = config.kafkaTemplate(pf);

        // Assert
        assertSame(pf, template.getProducerFactory());
    }

    @Test
    @DisplayName("objectMapper não deve ser nulo")
    void objectMapper_should_not_be_null() {
        // Arrange & Act
        ObjectMapper om = config.objectMapper();

        // Assert
        assertNotNull(om);
    }
}