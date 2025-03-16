package com.berden.brokerage.unit.controller;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.controller.OrderController;
import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.helpers.OrderTestHelper;
import com.berden.brokerage.security.UserPrincipal;
import com.berden.brokerage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private final Long orderId = 1L;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderController orderController;

    private UserPrincipal currentUser;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/api/orders");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        currentUser = new UserPrincipal(1L, "customer", "strongpass", Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")), 1L);

    }

    @Test
    void createOrder_ShouldReturnCreatedOrderWithLocationHeader() {
        Order mockOrder = OrderTestHelper.createAppleBuyOrderWithId();
        OrderRequest orderRequest = OrderTestHelper.appleBuyRequest;
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(mockOrder);

        ResponseEntity<OrderResponse> response = orderController.createOrder(currentUser, orderRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("AAPL", response.getBody().assetName());
        assertEquals(OrderSide.BUY, response.getBody().orderSide());
        assertEquals(BigDecimal.valueOf(0.5), response.getBody().size());
        assertEquals(BigDecimal.valueOf(450.05), response.getBody().price());
        assertEquals(OrderStatus.PENDING, response.getBody().status());

        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().getPath().contains("" + orderId));

        verify(orderService).createOrder(any(OrderRequest.class));
    }

    @Test
    void getOrder_ShouldReturnOrder() {
        Order mockOrder = OrderTestHelper.createAppleBuyOrderWithId();
        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);


        ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().id());
        assertEquals("AAPL", response.getBody().assetName());
        assertEquals(OrderStatus.PENDING, response.getBody().status());

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void cancelOrder_ShouldReturnCancelledOrder() {
        Order cancelledOrder = Order.builder()
                .id(orderId)
                .customerId(1L)
                .assetName("BTC")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(40000))
                .status(OrderStatus.CANCELED)
                .createDate(LocalDateTime.now())
                .build();

        when(orderService.cancelOrder(orderId, 1L)).thenReturn(cancelledOrder);

        ResponseEntity<OrderResponse> response = orderController.cancelOrder(currentUser, orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().id());
        assertEquals(OrderStatus.CANCELED, response.getBody().status());

        verify(orderService).cancelOrder(orderId, 1L);
    }
}