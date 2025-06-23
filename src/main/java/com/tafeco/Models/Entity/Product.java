package com.tafeco.Models.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="products")
public class Product  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="product")
    private String product;

    @Column(name="price")
    private double price;

    @Column(length = 3500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name="categorise")
    private Categorise categorise;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Photo> photos = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Archive> archives = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "dimension_id")
    private Dimension dimension;


    @OneToMany(mappedBy = "product")
    private Set<Store> stores = new HashSet<>();
}
