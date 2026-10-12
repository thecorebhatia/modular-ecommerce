package org.ecommerce.product.data;

import jakarta.persistence.Id;
import org.ecommerce.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Id> {
}
