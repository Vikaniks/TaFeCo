package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.ProductTransferRequestDTO;

import java.util.List;

public interface IProductInventoryService {
    void transferProductsToStore(List<ProductTransferRequestDTO> transferRequests);
}

