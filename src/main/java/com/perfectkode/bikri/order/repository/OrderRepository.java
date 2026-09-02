package com.perfectkode.bikri.order.repository;


import com.perfectkode.bikri.order.model.Order;
import com.perfectkode.bikri.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Fetch paginated order history for a specific authenticated user
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    // Fetch single order verifying ownership
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    // Fetch paginated orders by status (For Admin management)
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
