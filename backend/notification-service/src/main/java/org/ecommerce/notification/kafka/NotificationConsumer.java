package org.ecommerce.notification.kafka;

import jakarta.annotation.PostConstruct;
import org.ecommerce.notification.data.ProcessedEventRepository;
import org.ecommerce.notification.dto.OrderCreatedEvent;
import org.ecommerce.notification.model.ProcessedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public NotificationConsumer(ProcessedEventRepository processedEventRepository, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initCheck() {
        System.out.println("🚀 CRITICAL SYSTEM LOG: NotificationConsumer has successfully booted and loaded into the Spring Context!");
    }

    @KafkaListener(
            topics = "order-events-topic",
            groupId = "ecom-notification-group-test",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderNotification(String eventJson) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(eventJson, OrderCreatedEvent.class);
            String messageId = event.orderId().toString();

            if (processedEventRepository.existsById(messageId)) {
                System.out.println("⚠️ LOG: Duplicate message detected. Skipping event ID: " + messageId);
                return;
            }

            processedEventRepository.save(new ProcessedEvent(messageId, LocalDateTime.now()));
            System.out.println("==================================Message Received====================");
            System.out.println("\n📬 LOG: Notification Service processing event for order: " + messageId);
        } catch (Exception e) {
            System.err.println("❌ Error processing notification event: " + e.getMessage());
        }
    }
}
