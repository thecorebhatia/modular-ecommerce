package org.ecommerce.product.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductServiceResponse (
        ServiceStatus serviceStatus,
        List<ProductResponse> productResponse
)
{}
