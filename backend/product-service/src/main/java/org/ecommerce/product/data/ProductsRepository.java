package org.ecommerce.product.data;

import jakarta.persistence.Id;
import org.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Id> {
    Optional<Product> findByProductId(UUID productId);
    Optional<Product> deleteByProductId(UUID productId);
    List<Product> findAllByStatus(String status);

}
