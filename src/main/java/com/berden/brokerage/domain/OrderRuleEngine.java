package com.berden.brokerage.domain;

import com.berden.brokerage.common.enums.Rules;
import com.berden.brokerage.entity.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderRuleEngine {
    private final Map<Rules, List<OrderRule>> rulesByOperation = new HashMap<>();

    public OrderRuleEngine() {
        // cancel rules
        this.addRule(Rules.CANCEL_ORDER, OrderRules::validateOrderStatusPending);
        this.addRule(Rules.CANCEL_ORDER, OrderRules::validateOrderBelongsToCustomer);

        // match rules
        this.addRule(Rules.MATCH_ORDER, OrderRules::validateOrderStatusPending);
    }

    public void addRule(Rules operation, OrderRule rule) {
        rulesByOperation.computeIfAbsent(operation, k -> new ArrayList<>()).add(rule);
    }

    public void validate(Rules operation, Order order, Long customerId) {
        List<OrderRule> rules = rulesByOperation.get(operation);
        if (rules != null) {
            for (OrderRule rule : rules) {
                rule.validate(order, customerId);
            }
        }
    }
}
