package org.ecommerce.product.controller;

import org.ecommerce.product.dto.ProductRequest;
import org.ecommerce.product.dto.ProductServiceResponse;
import org.ecommerce.product.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/fetch")
    public ProductServiceResponse getAll() {
        return productService.getAll();
    }

    @GetMapping("/fetch/{productId}")
    public ProductServiceResponse get(@PathVariable UUID productId) {
        return productService.get(productId);
    }

    @PostMapping("/update")
    public ProductServiceResponse add(@RequestBody ProductRequest request) {
        return productService.add(request);
    }

    @PutMapping("/update/{productId}")
    public ProductServiceResponse update(@RequestBody ProductRequest request, @PathVariable UUID productId) {
        return productService.update(request, productId);
    }

    @DeleteMapping("/update/{productId}")
    public ProductServiceResponse delete(@PathVariable UUID productId) {
        return productService.delete(productId);
    }
}
