package com.tafeco.Models.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "locality_id")
    private Locality locality;

    @ManyToOne
    @JoinColumn(name = "street_id")
    private Street street;

    @Column(nullable = true)
    private String house;

    @Column(nullable = true)
    private String apartment;

    @Column(nullable = true)
    private String addressExtra;

}

