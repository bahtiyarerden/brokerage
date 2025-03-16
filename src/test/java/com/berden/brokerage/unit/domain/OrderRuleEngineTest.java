package com.berden.brokerage.unit.domain;

import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.common.enums.Rules;
import com.berden.brokerage.domain.OrderRuleEngine;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.InvalidOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderRuleEngineTest {
    private OrderRuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new OrderRuleEngine();
    }

    @Test
    void validate_ShouldApplyAllRulesForCancelOrder() {
        Order order = Order.builder().status(OrderStatus.PENDING).customerId(1L).build();
        assertDoesNotThrow(() -> ruleEngine.validate(Rules.CANCEL_ORDER, order, 1L));
    }

    @Test
    void validate_ShouldThrowException_WhenCancelOrderRulesFail() {
        Order order = Order.builder().status(OrderStatus.MATCHED).customerId(1L).build();
        assertThrows(InvalidOrderException.class, () -> ruleEngine.validate(Rules.CANCEL_ORDER, order, 1L));
    }

    @Test
    void validate_ShouldApplyMatchOrderRulesSuccessfully() {
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        assertDoesNotThrow(() -> ruleEngine.validate(Rules.MATCH_ORDER, order, null));
    }
}
