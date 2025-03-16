package com.berden.brokerage.helpers;

import com.berden.brokerage.common.constants.AssetsConstant;
import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Asset;

import java.math.BigDecimal;

public class AssetTestHelper {
    public static OrderableAsset orderableAppleAssetBUY = new OrderableAsset(1L, "AAPL", OrderSide.BUY, BigDecimal.valueOf(1), BigDecimal.valueOf(100));
    public static OrderableAsset orderableAppleAssetSELL = new OrderableAsset(1L, "AAPL", OrderSide.SELL, BigDecimal.valueOf(1), BigDecimal.valueOf(1.2));

    public static OrderableAsset orderableMETAAssetBUY = new OrderableAsset(1L, "META", OrderSide.BUY, BigDecimal.valueOf(101), BigDecimal.valueOf(100));

    public static Asset generateAppleAsset() {
        return Asset.builder()
                .customerId(1L)
                .assetName("AAPL")
                .size(BigDecimal.valueOf(1.8))
                .usableSize(BigDecimal.valueOf(1.2))
                .build();
    }

    public static Asset generateTRYAsset() {
        return Asset.builder()
                .customerId(1L)
                .assetName(AssetsConstant.TRY_ASSET)
                .size(BigDecimal.valueOf(10000))
                .usableSize(BigDecimal.valueOf(10000))
                .build();
    }

}
