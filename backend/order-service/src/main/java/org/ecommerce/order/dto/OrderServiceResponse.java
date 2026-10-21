package org.ecommerce.order.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record OrderServiceResponse(
        ServiceStatus serviceStatus,
        List<OrderResponse> orderResponse
)
{}
