package com.berden.brokerage.unit.service;

import com.berden.brokerage.common.constants.AssetsConstant;
import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.exception.InsufficientBalanceException;
import com.berden.brokerage.helpers.AssetTestHelper;
import com.berden.brokerage.repository.AssetRepository;
import com.berden.brokerage.service.AssetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @InjectMocks
    private AssetServiceImpl assetService;

    @Mock
    private AssetRepository assetRepository;

    @Test
    public void reserveAssetForOrder_WhenBuyOrderAndSufficientBalance_ShouldReserveAmount() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetBUY;
        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        assetService.reserveAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(9900), assetTRY.getUsableSize());


        verify(assetRepository).save(assetTRY);
    }

    @Test
    public void reserveAssetForOrder_WhenBuyOrderAndInsufficientBalance_ShouldThrowException() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableMETAAssetBUY;

        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        assertThrows(InsufficientBalanceException.class, () -> assetService.reserveAssetForOrder(orderableAsset));

        assertEquals(BigDecimal.valueOf(10000), assetTRY.getUsableSize());
        verify(assetRepository, never()).save(any());
    }

    @Test
    public void reserveAssetForOrder_WhenSellOrderAndSufficientBalance_ShouldReserveAsset() {
        Asset appleAsset = AssetTestHelper.generateAppleAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetSELL;

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(appleAsset));

        assetService.reserveAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(0.2), appleAsset.getUsableSize());

        verify(assetRepository).save(appleAsset);
    }

    @Test
    public void reserveAssetForOrder_WhenSellOrderAndInsufficientBalance_ShouldThrowException() {
        Asset appleAsset = AssetTestHelper.generateAppleAsset();
        OrderableAsset assetNotSellable =
                new OrderableAsset(1L, "AAPL", OrderSide.SELL, BigDecimal.valueOf(10), BigDecimal.valueOf(10000));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(appleAsset));

        assertThrows(InsufficientBalanceException.class, () -> assetService.reserveAssetForOrder(assetNotSellable));

        assertEquals(BigDecimal.valueOf(1.2), appleAsset.getUsableSize());
        verify(assetRepository, never()).save(any());
    }

    @Test
    public void releaseReservedAssetForOrder_WhenBuyOrder_ShouldRestoreUsableBalance() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetBUY;

        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        assetService.releaseReservedAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(10100), assetTRY.getUsableSize());

        verify(assetRepository).save(assetTRY);
    }

    @Test
    public void releaseReservedAssetForOrder_WhenSellOrder_ShouldRestoreSellableAsset() {
        Asset appleAsset = AssetTestHelper.generateAppleAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetSELL;

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(appleAsset));

        assetService.releaseReservedAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(2.2), appleAsset.getUsableSize());

        verify(assetRepository).save(appleAsset);
    }

    @Test
    public void matchReservedAssetForOrder_WhenBuyOrderAndAssetExists_ShouldIncreaseAssetBalance() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        Asset appleAsset = AssetTestHelper.generateAppleAsset();

        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetBUY;

        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(appleAsset));

        assetService.matchReservedAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(9900), assetTRY.getSize());
        assertEquals(BigDecimal.valueOf(2.8), appleAsset.getSize());
        assertEquals(BigDecimal.valueOf(2.2), appleAsset.getUsableSize());

        verify(assetRepository).save(assetTRY);
        verify(assetRepository).save(appleAsset);
    }

    @Test
    public void matchReservedAssetForOrder_WhenBuyOrderAndAssetDoesNotExist_ShouldCreateNewAsset() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetBUY;
        Asset appleAsset = orderableAsset.toAsset();

        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.empty());

        assetService.matchReservedAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(9900), assetTRY.getSize());

        verify(assetRepository).save(assetTRY);
        verify(assetRepository).save(appleAsset);
    }

    @Test
    public void matchReservedAssetForOrder_WhenSellOrder_ShouldDecreaseAssetBalanceAndIncreaseTRYBalance() {
        Asset assetTRY = AssetTestHelper.generateTRYAsset();
        Asset appleAsset = AssetTestHelper.generateAppleAsset();

        OrderableAsset orderableAsset = AssetTestHelper.orderableAppleAssetSELL;

        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetsConstant.TRY_ASSET))
                .thenReturn(Optional.of(assetTRY));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(appleAsset));

        assetService.matchReservedAssetForOrder(orderableAsset);

        assertEquals(BigDecimal.valueOf(10001.2), assetTRY.getSize());
        assertEquals(BigDecimal.valueOf(0.6), appleAsset.getSize());

        verify(assetRepository, times(2)).save(any(Asset.class));
    }
}
