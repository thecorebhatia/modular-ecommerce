package org.ecommerce.order.dto;

import java.util.UUID;

public record OrderResponse(UUID orderId, String username, long totalAmount, String status) {}
