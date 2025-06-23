package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.DTO.Mappers.ProductMapper;
import com.tafeco.DTO.Mappers.StoreMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.DAO.IStoreDAO;
import com.tafeco.Models.DAO.IWarehouseDAO;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.Warehouse;
import com.tafeco.Models.Services.Impl.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements IStoreService {

    private final IProductDAO productRepository;
    private final IStoreDAO storeRepository;
    private final IWarehouseDAO warehouseRepository;
    private final StoreMapper storeMapper;
    private final ProductMapper productMapper;



    @Override
    public StoreDTO create(StoreDTO storeDTO) {
        Product product = productRepository.findById(storeDTO.getProduct())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Warehouse warehouse = warehouseRepository.findById(storeDTO.getWarehouse())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Store store = storeMapper.toEntity(storeDTO, product, warehouse);
        return storeMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public StoreDTO update(Long id, StoreDTO storeDTO) {
        Store existing = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        existing.setMaxQuantity(storeDTO.getMaxQuantity());
        existing.setCurrentQuantity(storeDTO.getCurrentQuantity());

        // если нужно — обновляем product и warehouse
        if (!existing.getProduct().getId().equals(storeDTO.getProduct())) {
            Product product = productRepository.findById(storeDTO.getProduct())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            existing.setProduct(product);
        }

        if (!existing.getWarehouse().getId().equals(storeDTO.getWarehouse())) {
            Warehouse warehouse = warehouseRepository.findById(storeDTO.getWarehouse())
                    .orElseThrow(() -> new RuntimeException("Склад не найден"));
            existing.setWarehouse(warehouse);
        }

        return storeMapper.toDTO(storeRepository.save(existing));
    }

    @Override
    public boolean delete(Long id) {
        if (!storeRepository.existsById(id)) return false;
        storeRepository.deleteById(id);
        return true;
    }

    @Override
    public StoreDTO findById(Long id) {
        return storeRepository.findById(id)
                .map(storeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Точка продаж с ID " + id + " не найден"));
    }

    @Override
    public List<StoreDTO> findAll() {
        return storeRepository.findAll().stream()
                .map(storeMapper::toDTO)
                .toList();
    }

    @Override
    public List<StoreDTO> findByWarehouseId(Long warehouseId) {
        List<Store> stores = storeRepository.findByWarehouseId(warehouseId);
        return storeMapper.toDTOList(stores);
    }

    @Override
    public List<StoreDTO> findByWarehouseIdAndCurrentQuantityGreaterThan(Long warehouseId, int quantity) {
        List<Store> stores = storeRepository.findByWarehouseIdAndCurrentQuantityGreaterThan(warehouseId, quantity);
        return storeMapper.toDTOList(stores);
    }


    @Override
    public List<ProductStoreReportDTO> getStoreReportByProduct(Long productId) {
        // Проверка: существует ли продукт
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт с ID " + productId + " не найден"));

        // Получение всех записей Store, связанных с этим продуктом
        List<Store> stores = storeRepository.findByProductId(productId);

        // Формирование DTO-отчёта
        return stores.stream()
                .map(store -> {
                    ProductStoreReportDTO dto = new ProductStoreReportDTO();
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getProduct());
                    dto.setStoreId(store.getWarehouse().getId());
                    dto.setStoreLocation(store.getWarehouse().getLocation());
                    dto.setQuantity(store.getCurrentQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    }
