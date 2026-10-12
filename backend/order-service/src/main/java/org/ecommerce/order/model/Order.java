package org.ecommerce.order.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    private UUID orderId = UUID.randomUUID();

    private String username;
    private UUID productId;
    private int quantity;
    private long totalAmount;
    private OrderStatus status;
}
