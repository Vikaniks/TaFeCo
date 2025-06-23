package com.tafeco.Models.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="stores")
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 7178386848968642920L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "max_quantity", nullable = false)
    private int maxQuantity;

    @Column(name = "current_quantity", nullable = false)
    private int currentQuantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;


}
