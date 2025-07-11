package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.ProductTransferRequestDTO;
import com.tafeco.DTO.DTO.WarehouseDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;

import java.util.List;

public interface IWarehouseService {
    WarehouseDTO create(WarehouseDTO dto);
    WarehouseDTO getById(Long id);
    List<WarehouseDTO> getAll();
    WarehouseDTO update(Long id, WarehouseDTO dto);
    void delete(Long id);

    List<WarehouseStockDTO> getWarehouseStock(Long warehouseId);
    List<WarehouseStockDTO> getFullWarehouseStock();

    void deactivate(Long id);

    void activate(Long id);

    void receiveProduct(Long id, ProductTransferRequestDTO dto);
}
