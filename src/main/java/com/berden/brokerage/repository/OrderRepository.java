package com.berden.brokerage.repository;

import com.berden.brokerage.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetweenOrderByCreateDateDesc(
            Long customerId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT o.customerId FROM Order o WHERE o.id = :orderId")
    Optional<Long> findCustomerIdByOrderId(Long orderId);
}
