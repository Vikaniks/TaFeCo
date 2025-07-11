package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.*;
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

        List<ProductStoreReportDTO> getProductStoreReport(Long productId);


        List<WarehouseStockDTO> getStockByStore(Long storeId);

        List<WarehouseStockDTO> getFullStockForAllStores();

        void deactivateStore(Long id);

        void activateStore(Long id);

        void receiveProduct(Long id, ProductTransferResponseDTO dto);
}


