package org.ecommerce.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceStatus {
    private int statusCode;
    private boolean success;
    private String message;
}
