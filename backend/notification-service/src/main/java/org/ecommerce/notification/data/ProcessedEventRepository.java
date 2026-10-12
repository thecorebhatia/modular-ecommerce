package org.ecommerce.notification.data;

import jakarta.persistence.Id;
import org.ecommerce.notification.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {}
