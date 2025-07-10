package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.CreateStoreProductDTO;
import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.StoreProductDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;

import java.util.List;

public interface IStoreProductService {

    StoreProductDTO create(CreateStoreProductDTO dto);
    StoreProductDTO update(Long id, StoreProductDTO dto);
    boolean delete(Long id);
    List<WarehouseStockDTO> getFullStock();
    List<WarehouseStockDTO> getStockByStore(Long storeId);
    List<ProductStoreReportDTO> getStoreReportByProduct(Long productId);


}
