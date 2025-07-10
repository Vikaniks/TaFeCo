package com.tafeco.Models.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="stores")
public class Store  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "max_quantity", nullable = false)
    private int maxQuantity;

    @Column(name = "current_quantity", nullable = false)
    private int currentQuantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnore
    private Warehouse warehouse;


}
