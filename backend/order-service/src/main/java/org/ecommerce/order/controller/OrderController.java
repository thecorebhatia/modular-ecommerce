package org.ecommerce.order.controller;

import org.ecommerce.order.dto.OrderRequest;
import org.ecommerce.order.dto.OrderServiceResponse;
import org.ecommerce.order.service.OrderService;
import org.ecommerce.shared.rate_limiter.LimiterStrategy;
import org.ecommerce.shared.rate_limiter.RateLimited;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @RateLimited(LimiterStrategy.PER_ORDER)
    public OrderServiceResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping
    @RateLimited(LimiterStrategy.ENDPOINT)
    public OrderServiceResponse getOrders() {
        return orderService.getAll();
    }
}

