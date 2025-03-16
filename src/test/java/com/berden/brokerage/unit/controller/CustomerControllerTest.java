package com.berden.brokerage.unit.controller;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.controller.CustomerController;
import com.berden.brokerage.dto.external.response.AssetResponse;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.helpers.AssetTestHelper;
import com.berden.brokerage.helpers.OrderTestHelper;
import com.berden.brokerage.service.AssetService;
import com.berden.brokerage.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private final Long customerId = 1L;
    @InjectMocks
    private CustomerController customerController;
    @Mock
    private AssetService assetService;
    @Mock
    private OrderService orderService;

    @Test
    void getAssets_ShouldReturnAssetsList() {
        Asset appleAsset = AssetTestHelper.generateAppleAsset();
        Asset tryAsset = AssetTestHelper.generateTRYAsset();
        List<Asset> mockAssets = List.of(appleAsset, tryAsset);

        when(assetService.getAllAssets(customerId)).thenReturn(mockAssets);

        ResponseEntity<List<AssetResponse>> response = customerController.getAssets(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(assetService).getAllAssets(customerId);

        List<AssetResponse> responseBody = response.getBody();
        assertEquals("AAPL", responseBody.get(0).assetName());
        assertEquals(BigDecimal.valueOf(1.8), responseBody.get(0).size());
        assertEquals("TRY", responseBody.get(1).assetName());
        assertEquals(BigDecimal.valueOf(10000), responseBody.get(1).size());
    }

    @Test
    void getCustomerOrders_ShouldReturnOrdersList() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        Order appleBuyOrder = OrderTestHelper.createAppleBuyOrderWithId();
        List<Order> mockOrders = List.of(appleBuyOrder);

        when(orderService.getOrdersByCustomerAndDateRange(customerId, startDateTime, endDateTime))
                .thenReturn(mockOrders);

        ResponseEntity<List<OrderResponse>> response = customerController.getCustomerOrders(customerId, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(orderService)
                .getOrdersByCustomerAndDateRange(customerId, startDateTime, endDateTime);

        List<OrderResponse> responseBody = response.getBody();
        assertEquals("AAPL", responseBody.get(0).assetName());
        assertEquals(OrderSide.BUY, responseBody.get(0).orderSide());
        assertEquals(BigDecimal.valueOf(0.5), responseBody.get(0).size());
        assertEquals(BigDecimal.valueOf(450.05), responseBody.get(0).price());
        assertEquals(OrderStatus.PENDING, responseBody.get(0).status());
    }
}
