package com.berden.brokerage.dto.external.response;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(Long id, Long customerId, String assetName, OrderSide orderSide, BigDecimal size,
                            BigDecimal price, OrderStatus status, LocalDateTime createdDate) {

    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerId(), order.getAssetName(), order.getOrderSide(), order.getSize(), order.getPrice(), order.getStatus(), order.getCreateDate());
    }
}
