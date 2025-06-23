package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.ProductTransferRequestDTO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.DAO.IProductInventoryLocationDAO;
import com.tafeco.Models.Entity.LocationType;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.ProductInventoryLocation;
import com.tafeco.Models.Services.Impl.IProductInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductInventoryServiceImpl implements IProductInventoryService {

    private final IProductInventoryLocationDAO inventoryRepo;
    private final IProductDAO productRepository;

    @Transactional
    @Override
    public void transferProductsToStore(List<ProductTransferRequestDTO> transferRequests) {
        for (ProductTransferRequestDTO request : transferRequests) {
            // ищем продукт
            Product product = productRepository.findByProduct(request.getProductName());
            if (product == null) {
                throw new IllegalArgumentException(
                        "Продукт не найден: " + request.getProductName());
            }

            // ищем складскую запись
            ProductInventoryLocation warehouseLocation = inventoryRepo
                    .findByProductAndLocationTypeAndWarehouseId(
                            product, LocationType.WAREHOUSE, request.getSourceWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No warehouse stock for " + request.getProductName()));

            if (warehouseLocation.getQuantity() < request.getQuantityToTransfer()) {
                throw new IllegalStateException(
                        "Not enough stock in warehouse for product " + request.getProductName());
            }

            // уменьшаем склад
            warehouseLocation.setQuantity(
                    warehouseLocation.getQuantity() - request.getQuantityToTransfer());
            inventoryRepo.save(warehouseLocation);

            // увеличиваем в магазине
            ProductInventoryLocation storeLocation = inventoryRepo
                    .findByProductAndLocationTypeAndStoreId(
                            product, LocationType.STORE, request.getTargetStoreId())
                    .orElseGet(() -> {
                        ProductInventoryLocation newStoreLocation = new ProductInventoryLocation();
                        newStoreLocation.setProduct(product);
                        newStoreLocation.setLocationType(LocationType.STORE);
                        newStoreLocation.setStoreId(request.getTargetStoreId());
                        newStoreLocation.setQuantity(0);
                        return newStoreLocation;
                    });

            storeLocation.setQuantity(
                    storeLocation.getQuantity() + request.getQuantityToTransfer());
            inventoryRepo.save(storeLocation);
        }
    }
}
