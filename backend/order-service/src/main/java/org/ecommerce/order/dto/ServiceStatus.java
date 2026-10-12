package org.ecommerce.order.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {
    private int statusCode;
    private boolean success;
    private String message;
}
