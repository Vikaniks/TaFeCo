package com.tafeco.Models.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name="categories")
public class Categorise  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;


    @Column(unique = true)
    private String type;

    @JsonIgnore
    @OneToMany(mappedBy = "categorise", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();
}
