/*
package com.example.healthservice.kafka.producer;

import com.example.healthservice.event.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventProducer {

    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;
    private static final String TOPIC = "appointment-events";

    public CompletableFuture<SendResult<String, AppointmentEvent>> sendAppointmentEvent(AppointmentEvent event) {
        log.info("Sending appointment event to Kafka: {}", event);
        return kafkaTemplate.send(TOPIC, event.getAppointmentId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully to Kafka: {}", result.getRecordMetadata());
                    } else {
                        log.error("Unable to send message to Kafka: {}", ex.getMessage());
                    }
                });
    }
}
*/
