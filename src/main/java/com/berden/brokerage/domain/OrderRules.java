package com.berden.brokerage.domain;

import com.berden.brokerage.common.enums.OrderStatus;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.InvalidOrderException;

public class OrderRules {

    public static void validateOrderBelongsToCustomer(Order order, Long customerId) {
        if (customerId != null && !order.getCustomerId().equals(customerId)) {
            throw new InvalidOrderException("order does not belong to customer");
        }
    }

    public static void validateOrderStatusPending(Order order, Long customerId) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderException("only PENDING orders can be processed - current status is: " + order.getStatus());
        }
    }
}
