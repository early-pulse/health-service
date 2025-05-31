package com.example.healthservice.config;

import com.example.healthservice.dto.AppointmentDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Autowired
    private KafkaSSLConfig kafkaSSLConfig;

    @Bean
    public ProducerFactory<String, AppointmentDTO> producerFactory() {
        Map<String, Object> configProps = kafkaSSLConfig.getKafkaSSLConfig();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AppointmentDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
} 