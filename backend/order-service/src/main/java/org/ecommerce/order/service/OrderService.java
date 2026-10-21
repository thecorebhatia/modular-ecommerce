package org.ecommerce.order.service;

import jakarta.transaction.Transactional;
import org.ecommerce.order.client.ProductClient;
import org.ecommerce.order.data.OrderOutboxRepository;
import org.ecommerce.order.data.OrderRepository;
import org.ecommerce.order.dto.*;
import org.ecommerce.order.model.Order;
import org.ecommerce.order.model.OrderOutbox;
import org.ecommerce.order.model.OrderStatus;
import org.ecommerce.order.util.ServiceUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderOutboxRepository outboxRepository;
    private final OrderInventoryValidator inventoryValidator;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, OrderOutboxRepository outboxRepository, OrderInventoryValidator inventoryValidator) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.outboxRepository = outboxRepository;
        this.inventoryValidator = inventoryValidator;
    }

    @Transactional
    public OrderServiceResponse create(OrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!inventoryValidator.validateAndDeductStock(request.productId(), request.quantity())) {
            return ServiceUtil.getOrderServiceResponse(400, false, "Failed due to product SOLD out", null);
        }

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

        return ServiceUtil.getOrderServiceResponse(200, true, "order created successfully", null);
    }

    public OrderServiceResponse getAll() {
        List<Order> orderList = orderRepository.findAll();
        if(Optional.of(orderList).isEmpty()) {
            return ServiceUtil.getOrderServiceResponse(400, false, "Orders not available", null);
        }

        List<OrderResponse> orderResponseList = null;
        orderResponseList = orderList.stream()
                .filter(Objects::nonNull)
                .map(order1 -> OrderResponse.builder()
                        .orderId(order1.getOrderId())
                        .status(order1.getStatus().name())
                        .totalAmount(order1.getTotalAmount())
                        .username(order1.getUsername())
                        .build()).collect(Collectors.toList());
        // Have to write
        return ServiceUtil.getOrderServiceResponse(200, true, "successfully fetch order details", orderResponseList);
    }
}
