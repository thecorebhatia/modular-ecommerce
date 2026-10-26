package org.ecommerce.shared.rate_limiter;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterImpl implements RateLimiterEngine {

    private final StringRedisTemplate redisTemplate;
    public RateLimiterImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isAllowed(String clientKey, long capacity, long refillRate) {
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                long now = System.currentTimeMillis();

                // Track state updates safely across clusters via Optimistic Locking
                operations.watch(clientKey);
                Map<Object, Object> bucket = operations.opsForHash().entries(clientKey);

                long availableTokens;
                long lastRefillTimestamp;
                if (bucket.isEmpty()) {
                    availableTokens = capacity;
                    lastRefillTimestamp = now;
                } else {
                    availableTokens = Long.parseLong((String) bucket.get("tokens"));
                    lastRefillTimestamp = Long.parseLong((String) bucket.get("last_refill"));

                    long elapsedTime = now - lastRefillTimestamp;
                    long tokensToAdd = (elapsedTime / 1000) * refillRate;

                    if (tokensToAdd > 0) {
                        availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
                        lastRefillTimestamp = now;
                    }
                }

                if (availableTokens >= 1) {
                    availableTokens--;
                    operations.multi(); // Queue modifications inside atomic transaction block
                    operations.opsForHash().putAll(clientKey, Map.of(
                            "tokens", String.valueOf(availableTokens),
                            "last_refill", String.valueOf(lastRefillTimestamp)
                    ));
                    operations.expire(clientKey, 24, TimeUnit.HOURS);
                    return operations.exec();
                }
                operations.unwatch();
                return null;
            }
        });

        if (txResults == null) {
            return false; // Token balance empty
        }
        return !txResults.isEmpty(); // Empty lists evaluate as an optimistic verification failure
    }
}
