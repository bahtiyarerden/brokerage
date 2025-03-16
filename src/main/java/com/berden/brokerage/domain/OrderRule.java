package com.berden.brokerage.domain;

import com.berden.brokerage.entity.Order;

@FunctionalInterface
public interface OrderRule {
    void validate(Order order, Long customerId);
}
