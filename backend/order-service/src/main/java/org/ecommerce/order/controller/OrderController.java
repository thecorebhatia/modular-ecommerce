package org.ecommerce.order.controller;

import org.ecommerce.order.dto.OrderRequest;
import org.ecommerce.order.dto.OrderResponse;
import org.ecommerce.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) throws ExecutionException, InterruptedException {
        return orderService.create(request);
    }
}

