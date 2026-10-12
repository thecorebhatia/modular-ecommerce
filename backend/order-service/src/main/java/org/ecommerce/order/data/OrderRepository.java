package org.ecommerce.order.data;

import jakarta.persistence.Id;
import org.ecommerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Id> {
}
