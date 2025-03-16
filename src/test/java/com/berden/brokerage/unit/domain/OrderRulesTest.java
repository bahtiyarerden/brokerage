package com.berden.brokerage.unit.domain;

import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.domain.OrderRules;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.InvalidOrderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRulesTest {

    @Test
    void validateOrderBelongsToCustomer_ShouldThrowException_WhenOrderDoesNotBelongToCustomer() {
        Order order = Order.builder().customerId(1L).build();
        Long otherCustomerId = 2L;

        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> OrderRules.validateOrderBelongsToCustomer(order, otherCustomerId));
        assertEquals("order does not belong to customer", exception.getMessage());
    }

    @Test
    void validateOrderBelongsToCustomer_ShouldNotThrowException_WhenOrderBelongsToCustomer() {
        Order order = Order.builder().customerId(1L).build();

        assertDoesNotThrow(() -> OrderRules.validateOrderBelongsToCustomer(order, 1L));
    }

    @Test
    void validateOrderStatusPending_ShouldThrowException_WhenOrderStatusIsNotPending() {
        Order order = Order.builder().status(OrderStatus.MATCHED).build();

        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> OrderRules.validateOrderStatusPending(order, null));
        assertTrue(exception.getMessage().contains("only PENDING orders can be processed"));
    }

    @Test
    void validateOrderStatusPending_ShouldNotThrowException_WhenOrderStatusIsPending() {
        Order order = Order.builder().status(OrderStatus.PENDING).build();

        assertDoesNotThrow(() -> OrderRules.validateOrderStatusPending(order, null));
    }

}
