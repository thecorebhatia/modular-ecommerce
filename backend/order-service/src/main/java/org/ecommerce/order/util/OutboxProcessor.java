package org.ecommerce.order.util;

import jakarta.transaction.Transactional;
import org.ecommerce.order.data.OrderOutboxRepository;
import org.ecommerce.order.model.OrderOutbox;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxProcessor {

    private final OrderOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxProcessor(OrderOutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000) // 👈 Adjust processing interval frequency as required
    @Transactional
    public void processOutbox() {
        List<OrderOutbox> unprocessedMessages = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OrderOutbox message : unprocessedMessages) {
            try {
                // Publish to Kafka and wait for acknowledgment
                SendResult<String, String> result = kafkaTemplate
                        .send("order-events-topic", message.getAggregateId(), message.getPayload())
                        .get();

                System.out.println("✅ [ORDER-SERVICE]: Message safely written to Partition: "
                        + result.getRecordMetadata().partition() +
                        " at Offset: " + result.getRecordMetadata().offset() +
                        " at Topic: " + result.getRecordMetadata().topic() +
                        " at Headers: " + result.getProducerRecord().headers()
                );

                // Mark as processed upon successful publishing
                message.setProcessed(true);
                outboxRepository.save(message);
            } catch (Exception e) {
                System.err.println("❌ Failed to publish message " + message.getId() + ": " + e.getMessage());
                break;
            }
        }
    }
}

