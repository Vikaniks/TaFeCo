package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.DimensionDTO;

import java.util.List;

public interface IDimensionService {
    DimensionDTO create(DimensionDTO dto);
    DimensionDTO getById(Long id);
    List<DimensionDTO> getAll();
    DimensionDTO update(Long id, DimensionDTO dto);
    boolean deleteDimension(Long id);
}

