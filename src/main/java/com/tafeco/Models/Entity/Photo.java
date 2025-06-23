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
@ToString
@Entity
@Table(name="photos")
public class Photo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2669028536370631121L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String photo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
}
