package com.berden.brokerage.controller;


import com.berden.brokerage.dto.external.response.AssetResponse;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.service.AssetService;
import com.berden.brokerage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    public AssetService assetService;

    @Autowired
    public OrderService orderService;

    @GetMapping("/{id}/assets")
    public ResponseEntity<List<AssetResponse>> getAssets(@PathVariable Long id) {
        List<Asset> assets = assetService.getAllAssets(id);
        return ResponseEntity.ok(assets.stream().map(AssetResponse::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderService.getOrdersByCustomerAndDateRange(id, startDateTime, endDateTime);
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderResponses);
    }
}
