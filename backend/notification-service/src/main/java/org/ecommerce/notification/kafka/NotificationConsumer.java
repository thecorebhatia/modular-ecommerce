package org.ecommerce.notification.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.ecommerce.notification.data.ProcessedEventRepository;
import org.ecommerce.notification.dto.OrderCreatedEvent;
import org.ecommerce.notification.model.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public NotificationConsumer(ProcessedEventRepository processedEventRepository, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initCheck() {
        log.info("NotificationConsumer booted into the Spring context");
    }

    @KafkaListener(
            topics = "order-events-topic",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderNotification(ConsumerRecord<String, String> record) {
        log.info("Message received topic={} partition={} offset={} key={} value={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());

        try {
            OrderCreatedEvent event = objectMapper.readValue(record.value(), OrderCreatedEvent.class);
            String messageId = event.orderId().toString();

            if (processedEventRepository.existsById(messageId)) {
                log.warn("Duplicate message detected. Skipping event ID: {}", messageId);
                return;
            }

            processedEventRepository.save(new ProcessedEvent(messageId, LocalDateTime.now()));
            log.info("Notification Service processing event for order: {}", messageId);
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
            throw e instanceof RuntimeException re ? re : new RuntimeException(e);
        }
    }
}
