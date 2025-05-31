package com.example.healthservice.config;

import com.example.healthservice.dto.AppointmentDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private KafkaSSLConfig kafkaSSLConfig;

    @Bean
    public ConsumerFactory<String, AppointmentDTO> consumerFactory() {
        Map<String, Object> configProps = kafkaSSLConfig.getKafkaSSLConfig();
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonDeserializer");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.healthservice.dto");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
} 