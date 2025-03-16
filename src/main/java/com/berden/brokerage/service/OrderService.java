package com.berden.brokerage.service;

import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order createOrder(OrderRequest request);

    Order cancelOrder(Long orderId, Long customerId);

    Order matchOrder(Long orderId);

    Order getOrderById(Long orderId);

    Long getCustomerIdByOrderId(Long orderId);

    List<Order> getOrdersByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
