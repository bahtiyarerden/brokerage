package com.berden.brokerage.dto.external.request;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.entity.Order;

import java.math.BigDecimal;

public record OrderableAsset(Long customerId, String assetName, OrderSide side, BigDecimal size, BigDecimal price) {

    public static OrderableAsset fromOrderEntity(Order order) {
        return new OrderableAsset(
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide(),
                order.getSize(),
                order.getPrice()
        );
    }

    public Asset toAsset() {
        return Asset.builder()
                .customerId(this.customerId())
                .assetName(this.assetName())
                .size(this.size())
                .usableSize(this.size())
                .build();
    }
}
