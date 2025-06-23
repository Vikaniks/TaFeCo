package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.Models.Entity.Store;

import java.util.List;
import java.util.Optional;

public interface IStoreService {

        StoreDTO create(StoreDTO storeDTO);
        StoreDTO update(Long id, StoreDTO storeDTO);
        boolean delete(Long id);
        StoreDTO findById(Long id);
        List<StoreDTO> findAll();
        List<StoreDTO> findByWarehouseId(Long warehouseId);
        List<StoreDTO> findByWarehouseIdAndCurrentQuantityGreaterThan(Long warehouseId, int quantity);

        List<ProductStoreReportDTO> getStoreReportByProduct(Long productId);



}


