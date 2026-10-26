package org.ecommerce.shared.rate_limiter;

public enum LimiterStrategy {
    GLOBAL("global", 10000, 2000),
    ENDPOINT("endpoint", 30, 5),
    PER_IP("ip_order", 3, 1),
    PER_ORDER("business_order", 1, 1);

    private final String prefix;
    private final long capacity;
    private final long refillRate;

    LimiterStrategy(String prefix, long capacity, long refillRate) {
        this.prefix = prefix;
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public String getPrefix() { return prefix; }
    public long getCapacity() { return capacity; }
    public long getRefillRate() { return refillRate; }
}
