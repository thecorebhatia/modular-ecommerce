package org.ecommerce.product.data;

import jakarta.persistence.Id;
import org.ecommerce.product.model.Product;
import org.ecommerce.product.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(UUID productId);
    Optional<Product> deleteByProductId(UUID productId);
    List<ProductStatus> findAllByStatus(String status);

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();
}
