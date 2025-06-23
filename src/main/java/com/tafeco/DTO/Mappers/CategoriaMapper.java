package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.Models.Entity.Categorise;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {
    CategoriaDTO toDTO(Categorise categorise);
    Categorise toEntity(CategoriaDTO dto);
    List<CategoriaDTO> toDTOList(List<Categorise> list);
}
