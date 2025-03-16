package com.berden.brokerage.unit.repository;

import com.berden.brokerage.common.enums.OrderSide;
import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.helpers.OrderTestHelper;
import com.berden.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveOrderAndFindById() {
        Order order = OrderTestHelper.createAppleBuyOrder();
        Order savedOrder = orderRepository.save(order);

        Optional<Order> maybeOrder = orderRepository.findById(savedOrder.getId());

        assertTrue(maybeOrder.isPresent());
        assertEquals("AAPL", maybeOrder.get().getAssetName());
        assertEquals(OrderSide.BUY, maybeOrder.get().getOrderSide());
        assertEquals(OrderStatus.PENDING, maybeOrder.get().getStatus());
    }

    @Test
    public void testFindCustomerIdByOrderId() {
        Order order = OrderTestHelper.createAppleBuyOrder();
        Order savedOrder = orderRepository.save(order);

        Optional<Long> maybeCustomerId = orderRepository.findCustomerIdByOrderId(savedOrder.getId());

        assertTrue(maybeCustomerId.isPresent());
        assertEquals(1L, maybeCustomerId.get());
    }

    @Test
    public void testOrderSideAndStatus() {
        Order sellOrder = Order.builder()
                .customerId(2L)
                .assetName("META")
                .orderSide(OrderSide.SELL)
                .size(BigDecimal.valueOf(1.2))
                .price(BigDecimal.valueOf(32.50))
                .status(OrderStatus.CANCELED)
                .createDate(LocalDateTime.now())
                .build();

        Order savedSellOrder = orderRepository.save(sellOrder);
        Optional<Order> maybeSellOrder = orderRepository.findById(savedSellOrder.getId());

        assertTrue(maybeSellOrder.isPresent());
        assertEquals(OrderSide.SELL, maybeSellOrder.get().getOrderSide());
        assertEquals(OrderStatus.CANCELED, maybeSellOrder.get().getStatus());
    }
}
