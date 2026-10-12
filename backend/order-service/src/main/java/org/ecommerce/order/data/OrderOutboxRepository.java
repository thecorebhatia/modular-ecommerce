package org.ecommerce.order.data;

import org.ecommerce.order.model.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, Long> {
    List<OrderOutbox> findByProcessedFalseOrderByCreatedAtAsc();
}
