package org.ecommerce.product.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductResponse(
        UUID productId,
        String title,
        long actualPrice,
        long finalPrice, // Evaluated dynamic discount threshold price
        String categoryName
) {}
