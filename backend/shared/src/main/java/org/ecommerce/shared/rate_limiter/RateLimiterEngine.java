package org.ecommerce.shared.rate_limiter;

public interface RateLimiterEngine {
    boolean isAllowed(String clientKey, long capacity, long refillRate);
}
