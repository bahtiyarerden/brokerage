package com.berden.brokerage.service;

import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.common.enums.Rules;
import com.berden.brokerage.domain.OrderRuleEngine;
import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.ResourceNotFoundException;
import com.berden.brokerage.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private OrderRuleEngine ruleEngine;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') || @brokerageSecurityExpression.isResourceOwner(#request.customerId)")
    public Order createOrder(OrderRequest request) {
        OrderableAsset orderableAsset = request.toAsset();
        assetService.reserveAssetForOrder(orderableAsset);

        Order order = Order.builder()
                .customerId(request.customerId())
                .assetName(request.assetName())
                .orderSide(request.side())
                .size(request.size())
                .price(request.price())
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();


        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') || @brokerageSecurityExpression.isResourceOwner(#customerId)")
    public Order cancelOrder(Long orderId, Long customerId) {
        Order order = getOrderById(orderId);
        ruleEngine.validate(Rules.CANCEL_ORDER, order, customerId);

        assetService.releaseReservedAssetForOrder(OrderableAsset.fromOrderEntity(order));
        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Order matchOrder(Long orderId) {
        Order order = getOrderById(orderId);
        ruleEngine.validate(Rules.MATCH_ORDER, order, null);

        assetService.matchReservedAssetForOrder(OrderableAsset.fromOrderEntity(order));
        order.setStatus(OrderStatus.MATCHED);
        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') || @brokerageSecurityExpression.isResourceOwnerByOrderId(#orderId)")
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order not found with id: " + orderId));
    }

    @Override
    public Long getCustomerIdByOrderId(Long orderId) {
        return orderRepository.findCustomerIdByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order not found with id: " + orderId));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') || @brokerageSecurityExpression.isResourceOwner(#customerId)")
    public List<Order> getOrdersByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetweenOrderByCreateDateDesc(
                customerId, startDate, endDate);
    }
}
