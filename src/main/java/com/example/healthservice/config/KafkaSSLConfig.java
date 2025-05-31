package com.example.healthservice.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaSSLConfig {

    public Map<String, Object> getKafkaSSLConfig() {
        Map<String, Object> props = new HashMap<>();
        
        // SSL Configuration
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "none");
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, System.getProperty("java.home") + "/lib/security/cacerts");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "changeit");
        props.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        
        // SASL Configuration
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"Ashwini_Tiwari\" password=\"UqeDgjWfbCOGONdN6CCp2vKz3W5BIz\";");
        
        return props;
    }
} 