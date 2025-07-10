package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.Models.DAO.IProductStockDAO;
import com.tafeco.Models.DAO.IStoreDAO;
import com.tafeco.Models.Entity.ProductStock;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Services.Impl.IProductStockService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductStockServiceImpl implements IProductStockService {
/*    private final IStoreDAO storeRepository;
    private final IProductStockDAO productStockRepository;

    @Override
    @Transactional
    public void decreaseStock(Long productId, int quantity, Long warehouseId) {
        Store store = storeRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found for product " + productId + " in warehouse " + warehouseId));

        if (store.getCurrentQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock");
        }

        store.setCurrentQuantity(store.getCurrentQuantity() - quantity);
        storeRepository.save(store);
    }

    @Override
    @Transactional
    public void increaseStock(Long productId, int quantity, Long warehouseId) {
        Store store = storeRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Store not found for product " + productId + " in warehouse " + warehouseId));
        store.setCurrentQuantity(store.getCurrentQuantity() + quantity);
        storeRepository.save(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreReportDTO> getStockReport() {
        return productStockRepository.findAll().stream()
                .map(stock -> new ProductStoreReportDTO(
                        stock.getProduct().getId(),
                        stock.getProduct().getProduct(),
                        stock.getStore().getWarehouse().getId(),
                        stock.getStore().getWarehouse().getLocation(),
                        stock.getQuantity()
                ))
                .collect(Collectors.toList());
    }



    @Override
    public void replenish(Long productId, int amount) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        stock.setQuantity(stock.getQuantity() + amount);
        productStockRepository.save(stock);
    }

    @Override
    public void reduce(Long productId, int amount) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newQty = stock.getQuantity() - amount;
        if (newQty < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        stock.setQuantity(newQty);
        productStockRepository.save(stock);
    }
    
 */
}
