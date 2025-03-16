package com.berden.brokerage.service;

import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Asset;

import java.util.List;

public interface AssetService {
    Asset getAssetByCustomerIdAndName(Long customerId, String assetName);

    Asset createAsset(Asset asset);

    Asset updateAsset(Asset asset);

    void reserveAssetForOrder(OrderableAsset asset);

    void releaseReservedAssetForOrder(OrderableAsset asset);

    void matchReservedAssetForOrder(OrderableAsset asset);

    List<Asset> getAllAssets(Long customerId);
}
