package org.ecommerce.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.ecommerce.order.dto.ProductServiceResponse;
import org.ecommerce.order.dto.ServiceStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.UUID;

@Component
public class ProductClient {
    private final RestClient restClient;

    public ProductClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader("X-Internal-Call", "true")
                .build();
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    public ProductServiceResponse getProductDetails(UUID productId) {
        return restClient.get()
                .uri("/api/v1/products/fetch/{productId}", productId)
                .retrieve()
                .body(ProductServiceResponse.class);
    }

    private ProductServiceResponse fallback(UUID productId, Throwable ex) {
        System.err.println("Circuit Breaker triggered for product " + productId + ". Reason: " + ex.getMessage());
        ServiceStatus status = ServiceStatus.builder()
                        .message("Service unavailable to fetch product via " + productId)
                        .statusCode(503)
                        .success(false).build();
        return ProductServiceResponse.builder()
                .serviceStatus(status)
                .build();
    }
}

