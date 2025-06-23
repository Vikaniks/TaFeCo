package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.Product;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String product;
    private double price;
    private String description;

    private Integer categorise;
    private Long dimension;

    private Set<PhotoDTO> photos;
    private Set<ArchiveDTO> archives;

    private boolean active;

    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProduct(product.getProduct());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());

        // Категория
        if (product.getCategorise() != null) {
            dto.setCategorise(product.getCategorise().getId());
        }

        // Размерность
        if (product.getDimension() != null) {
            dto.setDimension(product.getDimension().getId());
        }

        // Фото
        if (product.getPhotos() != null) {
            dto.setPhotos(
                    product.getPhotos().stream()
                            .map(PhotoDTO::fromEntity)
                            .collect(Collectors.toSet())
            );
        }

        // Архивы
        if (product.getArchives() != null) {
            dto.setArchives(
                    product.getArchives().stream()
                            .map(ArchiveDTO::fromEntity)
                            .collect(Collectors.toSet())
            );
        }

        dto.setActive(product.isActive());

        return dto;
    }
}


