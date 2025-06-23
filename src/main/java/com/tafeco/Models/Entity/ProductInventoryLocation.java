package com.tafeco.Models.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_inventory_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType locationType;

    @Column
    private Long storeId;

    @Column
    private Long warehouseId;

    @Column(nullable = false)
    private int quantity;
}

