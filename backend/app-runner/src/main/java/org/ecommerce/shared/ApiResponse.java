package org.ecommerce.shared;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {}

