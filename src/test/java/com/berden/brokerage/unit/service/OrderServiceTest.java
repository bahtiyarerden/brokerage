package com.berden.brokerage.unit.service;

import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.common.enums.Rules;
import com.berden.brokerage.domain.OrderRuleEngine;
import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.dto.external.request.OrderableAsset;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.ResourceNotFoundException;
import com.berden.brokerage.helpers.OrderTestHelper;
import com.berden.brokerage.repository.OrderRepository;
import com.berden.brokerage.service.AssetService;
import com.berden.brokerage.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private OrderRuleEngine orderRuleEngine;

    @Test
    void createOrder_ShouldReserveAssetAndSaveOrder() {
        OrderRequest appleBuyRequest = OrderTestHelper.appleBuyRequest;
        OrderableAsset orderableAsset = appleBuyRequest.toAsset();
        Order order = OrderTestHelper.createAppleBuyOrder();

        doNothing().when(assetService).reserveAssetForOrder(orderableAsset);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(appleBuyRequest);

        verify(assetService).reserveAssetForOrder(orderableAsset);
        verify(orderRepository).save(any(Order.class));
        assertNotNull(createdOrder);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
    }

    @Test
    void cancelOrder_WhenOrderExists_ShouldReleaseReservedAssetAndCancelOrder() {
        Order order = OrderTestHelper.createAppleBuyOrder();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(assetService).releaseReservedAssetForOrder(any());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order canceledOrder = orderService.cancelOrder(1L, 1L);

        verify(orderRuleEngine).validate(Rules.CANCEL_ORDER, order, 1L);
        verify(assetService).releaseReservedAssetForOrder(any(OrderableAsset.class));
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.CANCELED, canceledOrder.getStatus());
    }

    @Test
    void cancelOrder_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(1L, 1L));
    }

    @Test
    void matchOrder_WhenOrderExists_ShouldMatchReservedAssetAndSetMatchedStatus() {
        Order order = OrderTestHelper.createAppleBuyOrder();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(assetService).matchReservedAssetForOrder(any());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order matchedOrder = orderService.matchOrder(1L);

        verify(orderRuleEngine).validate(Rules.MATCH_ORDER, order, null);
        verify(assetService).matchReservedAssetForOrder(any(OrderableAsset.class));
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.MATCHED, matchedOrder.getStatus());
    }

    @Test
    void matchOrder_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.matchOrder(1L));
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        Order order = OrderTestHelper.createAppleBuyOrder();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals("AAPL", foundOrder.getAssetName());
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getCustomerIdByOrderId_WhenOrderExists_ShouldReturnCustomerId() {
        when(orderRepository.findCustomerIdByOrderId(1L)).thenReturn(Optional.of(1L));

        Long customerId = orderService.getCustomerIdByOrderId(1L);

        assertNotNull(customerId);
        assertEquals(1L, customerId);
    }

    @Test
    void getCustomerIdByOrderId_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findCustomerIdByOrderId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getCustomerIdByOrderId(1L));
    }
}
