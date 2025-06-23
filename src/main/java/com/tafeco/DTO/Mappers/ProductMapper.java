package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.Models.Entity.Categorise;
import com.tafeco.Models.Entity.Dimension;
import org.mapstruct.*;
import com.tafeco.Models.Entity.Product;


@Mapper(
        componentModel = "spring",
        uses = { PhotoMapper.class, ArchiveMapper.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {

    // Entity → DTO
    @Mapping(source = "categorise.id", target = "categorise")
    @Mapping(source = "dimension.id", target = "dimension")
    ProductDTO toDTO(Product product);

    // DTO → Entity (новый объект)
    @Mapping(target = "categorise",
            source = "categorise",
            qualifiedByName = "idToCategorise")
    @Mapping(target = "dimension",
            source = "dimension",
            qualifiedByName = "idToDimension")
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "archives", ignore = true)
    Product toEntity(ProductDTO dto);

    // Обновление существующей сущности
    @Mapping(target = "categorise",
            source = "categorise",
            qualifiedByName = "idToCategorise")
    @Mapping(target = "dimension",
            source = "dimension",
            qualifiedByName = "idToDimension")
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "archives", ignore = true)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product existing);

    // Помощники для id → Entity
    @Named("idToCategorise")
    default Categorise idToCategorise(Integer id) {
        if (id == null) return null;
        Categorise c = new Categorise();
        c.setId(id);
        return c;
    }

    @Named("idToDimension")
    default Dimension idToDimension(Long id) {
        if (id == null) return null;
        Dimension d = new Dimension();
        d.setId(id);
        return d;
    }
}


