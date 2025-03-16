package com.berden.brokerage.service;

import com.berden.brokerage.common.constants.AssetsConstant;
import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.exception.InsufficientBalanceException;
import com.berden.brokerage.exception.ResourceNotFoundException;
import com.berden.brokerage.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public Asset getAssetByCustomerIdAndName(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("asset: %s not found for customer: %s", assetName, customerId)));
    }


    @Override
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    @Override
    public Asset updateAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void reserveAssetForOrder(OrderableAsset orderableAsset) {

        switch (orderableAsset.side()) {
            case BUY -> {
                Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), AssetsConstant.TRY_ASSET);
                BigDecimal requiredAmount = orderableAsset.price().multiply(orderableAsset.size());

                if (asset.getUsableSize().compareTo(requiredAmount) < 0)
                    throw new InsufficientBalanceException("could not BUY asset, insufficient balance of TRY");

                // In the demand phase, TRY is locked in purchase transactions because only our TRY assets are affected when purchasing.
                asset.setUsableSize(asset.getUsableSize().subtract(requiredAmount));
                updateAsset(asset);
            }
            case SELL -> {
                Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), orderableAsset.assetName());

                if (asset.getUsableSize().compareTo(orderableAsset.size()) < 0)
                    throw new InsufficientBalanceException("could not SELL asset, insufficient balance of " + orderableAsset.assetName());

                // In the demand phase, the asset to be sold is locked directly in the sale transaction.
                // Since the transaction has not yet taken place, we do not touch our TRY assets...
                asset.setUsableSize(asset.getUsableSize().subtract(orderableAsset.size()));
                updateAsset(asset);
            }
        }
    }

    @Override
    @Transactional
    public void releaseReservedAssetForOrder(OrderableAsset orderableAsset) {
        switch (orderableAsset.side()) {
            case BUY -> {
                Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), AssetsConstant.TRY_ASSET);
                BigDecimal reservedAmount = orderableAsset.price().multiply(orderableAsset.size());

                // release reserved amount from the usable size of our TRY assets
                asset.setUsableSize(asset.getUsableSize().add(reservedAmount));
                updateAsset(asset);
            }
            case SELL -> {
                Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), orderableAsset.assetName());

                // release reserved sellable stock size from our assets
                asset.setUsableSize(asset.getUsableSize().add(orderableAsset.size()));
                updateAsset(asset);
            }
        }

    }

    @Override
    @Transactional
    public void matchReservedAssetForOrder(OrderableAsset orderableAsset) {
        switch (orderableAsset.side()) {
            case BUY -> {
                Asset assetTRY = getAssetByCustomerIdAndName(orderableAsset.customerId(), AssetsConstant.TRY_ASSET);
                BigDecimal totalAmount = orderableAsset.price().multiply(orderableAsset.size());

                // buy is issued and our TRY assets balance should be updated
                assetTRY.setSize(assetTRY.getSize().subtract(totalAmount));
                updateAsset(assetTRY);

                try {
                    Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), orderableAsset.assetName());
                    increaseBalance(asset, orderableAsset.size());
                    updateAsset(asset);
                } catch (ResourceNotFoundException e) {
                    // If assets are not our property, we must add them
                    Asset newAsset = orderableAsset.toAsset();
                    createAsset(newAsset);
                }

            }
            case SELL -> {
                Asset assetTRY = getAssetByCustomerIdAndName(orderableAsset.customerId(), AssetsConstant.TRY_ASSET);
                BigDecimal totalAmount = orderableAsset.price().multiply(orderableAsset.size());

                // we sold the stock and our TRY assets balance is increased
                increaseBalance(assetTRY, totalAmount);
                updateAsset(assetTRY);

                // decrease balance of asset that we sold
                Asset asset = getAssetByCustomerIdAndName(orderableAsset.customerId(), orderableAsset.assetName());
                asset.setSize(asset.getSize().subtract(totalAmount));
                updateAsset(asset);
            }
        }

    }

    @Override
    @PreAuthorize("hasRole('ADMIN') || @brokerageSecurityExpression.isResourceOwner(#customerId)")
    public List<Asset> getAllAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    private void increaseBalance(Asset asset, BigDecimal amount) {
        asset.setSize(asset.getSize().add(amount));
        asset.setUsableSize(asset.getUsableSize().add(amount));
    }
}
