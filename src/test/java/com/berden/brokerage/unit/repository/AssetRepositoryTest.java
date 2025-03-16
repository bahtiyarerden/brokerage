package com.berden.brokerage.unit.repository;

import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.helpers.AssetTestHelper;
import com.berden.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AssetRepositoryTest {

    @Autowired
    private AssetRepository assetRepository;

    @Test
    public void testSaveAssetAndFindById() {
        Asset asset = AssetTestHelper.generateAppleAsset();
        Asset savedAsset = assetRepository.save(asset);

        Optional<Asset> maybeAsset = assetRepository.findById(savedAsset.getId());

        assertTrue(maybeAsset.isPresent());
        assertEquals("AAPL", maybeAsset.get().getAssetName());
    }

    @Test
    public void testFindByCustomerId() {
        Asset asset = AssetTestHelper.generateAppleAsset();
        assetRepository.save(asset);

        List<Asset> assets = assetRepository.findByCustomerId(1L);

        assertFalse(assets.isEmpty());
        assertEquals(1, assets.size());
        assertEquals("AAPL", assets.get(0).getAssetName());
    }

    @Test
    public void testFindByCustomerIdAndAssetName() {
        Asset asset = AssetTestHelper.generateAppleAsset();
        assetRepository.save(asset);

        Optional<Asset> foundAsset = assetRepository.findByCustomerIdAndAssetName(1L, "AAPL");

        assertTrue(foundAsset.isPresent());
        assertEquals(BigDecimal.valueOf(1.8), foundAsset.get().getSize());
    }

    @Test
    public void testUniqueCustomerIdAndAssetNameConstraint() {
        Asset asset = AssetTestHelper.generateAppleAsset();
        assetRepository.save(asset);

        Asset duplicateAsset = Asset.builder()
                .customerId(asset.getCustomerId())
                .assetName(asset.getAssetName())
                .size(BigDecimal.valueOf(0.8))
                .usableSize(BigDecimal.valueOf(0.8))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            assetRepository.save(duplicateAsset);
        });
    }
}
