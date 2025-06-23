package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.DimensionDTO;
import com.tafeco.DTO.DTO.PhotoDTO;
import com.tafeco.Models.Entity.Dimension;
import com.tafeco.Models.Entity.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DimensionMapper {
    DimensionDTO toDTO(Dimension dimension);
    Dimension toEntity(DimensionDTO dto);
    List<DimensionDTO> toDTOList(List<Dimension> list);
}
