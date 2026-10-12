package org.ecommerce.order.dto;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, String username, long totalAmount) {}

