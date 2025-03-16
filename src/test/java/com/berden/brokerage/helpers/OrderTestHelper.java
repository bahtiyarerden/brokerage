package com.berden.brokerage.helpers;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderTestHelper {
    public static OrderRequest appleBuyRequest = new OrderRequest(1L, "AAPL", OrderSide.BUY, BigDecimal.valueOf(0.5), BigDecimal.valueOf(450.05));
    public static OrderResponse appMatchedResponse = new OrderResponse(1L, 1L, "AAPL", OrderSide.BUY, BigDecimal.valueOf(0.5), BigDecimal.valueOf(450.05), OrderStatus.MATCHED, null);

    public static Order createAppleBuyOrder() {
        return Order.builder()
                .customerId(1L)
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(450.05))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
    }

    public static Order createAppleBuyOrderWithId() {
        return Order.builder()
                .id(1L)
                .customerId(1L)
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(450.05))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
    }
}
