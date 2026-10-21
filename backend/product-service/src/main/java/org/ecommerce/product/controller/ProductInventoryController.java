package org.ecommerce.product.controller;

import org.ecommerce.product.dto.ProductServiceResponse;
import org.ecommerce.product.model.ProductInventory;
import org.ecommerce.product.service.ProductInventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/inventory")
public class ProductInventoryController {

    private final ProductInventoryService productInventoryService;
    public ProductInventoryController(ProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    @PostMapping("/update")
    public ProductServiceResponse add(@RequestBody ProductInventory productInventory) {
        return productInventoryService.initializeOrUpdateStock(productInventory.getProductId(), productInventory.getStockCount());
    }

}
