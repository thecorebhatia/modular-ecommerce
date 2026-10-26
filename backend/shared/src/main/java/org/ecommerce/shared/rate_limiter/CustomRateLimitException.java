package org.ecommerce.shared.rate_limiter;

public class CustomRateLimitException extends RuntimeException {
    public CustomRateLimitException(String message) {
        super(message);
    }
}

