package com.berden.brokerage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "assets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "asset_name"})
)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(nullable = false)
    private BigDecimal size;

    @Column(name = "usable_size", nullable = false)
    private BigDecimal usableSize;
    
}
