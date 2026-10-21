package org.ecommerce.product.service;

import org.ecommerce.product.data.ProductInventoryRepository;
import org.ecommerce.product.dto.ProductServiceResponse;
import org.ecommerce.product.model.ProductInventory;
import org.ecommerce.product.util.ServiceUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductInventoryService {

    private final ProductInventoryRepository repository;
    private final StringRedisTemplate redisTemplate;
    public ProductInventoryService(ProductInventoryRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public ProductServiceResponse initializeOrUpdateStock(UUID productId, int initialStock) {
        if(Optional.of(productId).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide valid product id", null);
        }

        ProductInventory productInventory = ProductInventory.builder()
                .productId(productId)
                .stockCount(initialStock)
                .build();
        repository.save(productInventory);

        // 2. Seed into Redis Cache for high-speed launch testing
        String redisKey = "inventory:stock:" + productId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(initialStock));

        return ServiceUtil.getProductServiceResponse(200, true, "Product Count updated successfully", null);
    }
}
