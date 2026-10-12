package org.ecommerce.order.service;

import jakarta.transaction.Transactional;
import org.ecommerce.order.client.ProductClient;
import org.ecommerce.order.data.OrderOutboxRepository;
import org.ecommerce.order.data.OrderRepository;
import org.ecommerce.order.dto.*;
import org.ecommerce.order.model.Order;
import org.ecommerce.order.model.OrderOutbox;
import org.ecommerce.order.model.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderOutboxRepository outboxRepository;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, OrderOutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) throws ExecutionException, InterruptedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ProductServiceResponse productServiceResponse = productClient.getProductDetails(request.productId());

        long finalPrice = productServiceResponse.productResponse()
                .stream()
                .map(ProductResponse::finalPrice)
                .findFirst()
                .orElse(0l);
        long totalAmount = finalPrice * request.quantity();

        Order order = new Order();
        order.setUsername(username);
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), username, totalAmount);
        String jsonPayload = objectMapper.writeValueAsString(event);

        // 3. Save to Outbox Table in the SAME transaction
        OrderOutbox outbox = new OrderOutbox();
        outbox.setAggregateId(order.getOrderId().toString());
        outbox.setEventType("ORDER_CREATED");
        outbox.setPayload(jsonPayload);
        outboxRepository.save(outbox);

        return new OrderResponse(order.getOrderId(), username, totalAmount, order.getStatus().toString());
    }
}
