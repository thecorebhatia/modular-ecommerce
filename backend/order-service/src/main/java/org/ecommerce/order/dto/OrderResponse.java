package org.ecommerce.order.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderResponse(UUID orderId, String username, long totalAmount, String status) {}
