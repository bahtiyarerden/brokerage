package com.berden.brokerage.security;

import com.berden.brokerage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BrokerageSecurityExpression {

    @Autowired
    private OrderService orderService;


    public boolean isResourceOwner(Long resourceCustomerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getCustomerId().equals(resourceCustomerId);
    }

    public boolean isResourceOwnerByOrderId(Long orderId) {
        try {
            Long customerId = orderService.getCustomerIdByOrderId(orderId);
            return isResourceOwner(customerId);
        } catch (Exception e) {
            return false;
        }
    }
}
