package com.berden.brokerage.dto.external.request;

import com.berden.brokerage.common.enums.OrderSide;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull(message = "customerId is missing") Long customerId,
        @NotEmpty(message = "assetName is missing or empty") String assetName,
        @NotNull(message = "side is missing, use BUY/SELL instead") OrderSide side,
        @NotNull(message = "size is missing") @Positive(message = "size must be positive") BigDecimal size,
        @NotNull(message = "price is missing") @Positive(message = "price must be positive") BigDecimal price) {

    public OrderableAsset toAsset() {
        return new OrderableAsset(this.customerId, this.assetName, this.side, this.size, this.price);
    }
}
