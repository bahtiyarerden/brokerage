package com.berden.brokerage.controller;

import com.berden.brokerage.dto.external.request.OrderRequest;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.security.CurrentUser;
import com.berden.brokerage.security.UserPrincipal;
import com.berden.brokerage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @CurrentUser UserPrincipal currentUser,
            @RequestBody OrderRequest request) {

        Order createdOrder = orderService.createOrder(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();

        return ResponseEntity.created(location).body(OrderResponse.fromEntity(createdOrder));
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @DeleteMapping("/{orderId}")
//    @PreAuthorize("@brokerageSecurityExpression.isAdminOrOrderOwner(#orderId)")
    public ResponseEntity<OrderResponse> cancelOrder(@CurrentUser UserPrincipal currentUser,
                                                     @PathVariable Long orderId) {

        Order order = orderService.cancelOrder(orderId, currentUser.getCustomerId());
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }
}
