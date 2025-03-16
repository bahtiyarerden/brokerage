package com.berden.brokerage.controller;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.request.UserRegistrationRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.dto.external.response.UserResponse;
import com.berden.brokerage.dto.internal.UserDTO;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.service.AuthService;
import com.berden.brokerage.service.CustomerService;
import com.berden.brokerage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final AuthService authService;

    @Autowired
    public AdminController(CustomerService customerService,
                           OrderService orderService,
                           AuthService authService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping("/customers")
    private ResponseEntity<CustomerResponse> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        CustomerResponse customerResponse = customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @PostMapping("/users")
    private ResponseEntity<UserResponse> registerAdmin(@RequestBody UserRegistrationRequest request) {
        UserDTO userDto = authService.registerUser(request.username(), request.password(), Role.ADMIN, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDto(userDto));
    }

    @PutMapping("/orders/{orderId}/match")
    private ResponseEntity<OrderResponse> matchOrder(@PathVariable Long orderId) {
        Order order = orderService.matchOrder(orderId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

}
