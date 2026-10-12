package org.ecommerce.product.dto;

import org.ecommerce.product.model.Category;

public record ProductRequest(
        String title,
        long actualPrice,
        long discountPrice,
        Category category
) {}
