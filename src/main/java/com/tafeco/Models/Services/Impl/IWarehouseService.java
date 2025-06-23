package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.WarehouseDTO;

import java.util.List;

public interface IWarehouseService {
    WarehouseDTO create(WarehouseDTO dto);
    WarehouseDTO getById(Long id);
    List<WarehouseDTO> getAll();
    WarehouseDTO update(Long id, WarehouseDTO dto);
    void delete(Long id);
}
