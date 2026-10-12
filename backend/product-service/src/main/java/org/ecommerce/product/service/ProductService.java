package org.ecommerce.product.service;

import org.ecommerce.product.data.CategoryRepository;
import org.ecommerce.product.data.ProductsRepository;
import org.ecommerce.product.dto.ProductRequest;
import org.ecommerce.product.dto.ProductResponse;
import org.ecommerce.product.dto.ProductServiceResponse;
import org.ecommerce.product.model.Category;
import org.ecommerce.product.model.Product;
import org.ecommerce.product.model.ProductStatus;
import org.ecommerce.product.util.ServiceUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductsRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductsRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    public ProductServiceResponse get(UUID productId) {
        List<ProductResponse> productResponseList = new ArrayList<>();

        if(Optional.of(productId).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide valid product id", null);
        }

        Optional<Product> optionalProduct = repository.findByProductId(productId);
        if(optionalProduct.isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Invalid product id", null);
        }

        Product product = optionalProduct.get();
        productResponseList.add(ProductResponse.builder()
                .productId(product.getProductId())
                .actualPrice(product.getActualPrice())
                .categoryName(product.getCategory().getName())
                .finalPrice(product.getDiscountPrice())
                .title(product.getTitle())
                .build());
        return ServiceUtil.getProductServiceResponse(200, true, "successfully fetch product details", productResponseList);
    }

    public ProductServiceResponse getAll() {
        List<Product> product = repository.findAll();
        if(Optional.of(product).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Products not available", null);
        }

        List<ProductResponse> productResponseList = null;
        productResponseList = product.stream()
                .filter(Objects::nonNull)
                .map(product1 -> ProductResponse.builder()
                        .title(product1.getTitle())
                        .finalPrice(product1.getDiscountPrice())
                        .categoryName(product1.getCategory().getName())
                        .actualPrice(product1.getActualPrice())
                        .productId(product1.getProductId())
                        .build()).collect(Collectors.toList());
        // Have to write
        return ServiceUtil.getProductServiceResponse(200, true, "successfully fetch product details", productResponseList);
    }

    public ProductServiceResponse add(ProductRequest request) {
        if(Optional.of(request).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide proper request", null);
        }

        if(ServiceUtil.validateProductRequest(request)) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide proper request", null);
        }

        Product product = Product.builder()
                .title(request.title())
                .actualPrice(request.actualPrice())
                .discountPrice(request.discountPrice())
                .status(ProductStatus.ACTIVE)
                .productId(UUID.randomUUID())
                .build();

        Category category = Category.builder()
                        .name(request.category().getName())
                        .categoryId(UUID.randomUUID())
                        .description(request.category().getDescription())
                        .version("v1")
                        .build();


        // Mapped Category to Product
        product.setCategory(category);

        // Mapped Product to Category
        category.setProducts(Arrays.asList(product));
        categoryRepository.save(category);
        repository.save(product);
        return ServiceUtil.getProductServiceResponse(200, true, "Product added successfully", null);
    }

    public ProductServiceResponse update(ProductRequest request, UUID productId) {
        if(Optional.of(productId).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide valid product id", null);
        }

        if(Optional.of(request).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide proper request", null);
        }

        if(ServiceUtil.validateProductRequest(request)) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide proper request", null);
        }

        Optional<Product> optionalProduct = repository.findByProductId(productId);
        if(optionalProduct.isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Product id is not available", null);
        }

        Product product = optionalProduct.get();
        product.setActualPrice(request.actualPrice());
        product.setDiscountPrice(request.discountPrice());
        product.setTitle(request.title());

        repository.save(product);

        return ServiceUtil.getProductServiceResponse(200, true, "Product updated successfully", null);
    }

    public ProductServiceResponse delete(UUID productId) {
        if(Optional.of(productId).isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Please provide valid product id", null);
        }

        Optional<Product> optionalProduct = repository.findByProductId(productId);
        if(optionalProduct.isEmpty()) {
            return ServiceUtil.getProductServiceResponse(400, false, "Product id is not available", null);
        }

        repository.delete(optionalProduct.get());

        return ServiceUtil.getProductServiceResponse(200, true, "Product deleted successfully", null);
    }
}
