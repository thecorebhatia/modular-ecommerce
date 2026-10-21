package org.ecommerce.order.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderInventoryValidator {

    private final StringRedisTemplate redisTemplate;
    public OrderInventoryValidator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean validateAndDeductStock(UUID productId, int quantity) {
        String redisKey = "inventory:stock:" + productId;
        Long remaining = redisTemplate.opsForValue().decrement(redisKey, quantity);

        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(redisKey, quantity);
            return false;
        }

        return true;
    }
}
