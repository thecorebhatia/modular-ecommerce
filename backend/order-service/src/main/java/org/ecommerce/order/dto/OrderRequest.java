package org.ecommerce.order.dto;

import java.util.UUID;

public record OrderRequest(UUID productId, int quantity) {}

