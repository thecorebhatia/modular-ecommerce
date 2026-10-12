package org.ecommerce.order.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductServiceResponse(
        ServiceStatus serviceStatus,
        List<ProductResponse> productResponse
)
{}
