package com.tafeco.Models.Services;


import com.tafeco.DTO.DTO.ProductTransferResponseDTO;
import com.tafeco.DTO.DTO.WarehouseDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.DTO.Mappers.WarehouseMapper;
import com.tafeco.Models.DAO.*;
import com.tafeco.Models.Entity.LocationType;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.ProductInventoryLocation;
import com.tafeco.Models.Entity.Warehouse;
import com.tafeco.Models.Services.Impl.IWarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements IWarehouseService {

    private final IWarehouseDAO warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final IStoreProductDAO storeProductDAO;
    private final IProductDAO productDAO;
    private final IProductInventoryLocationDAO inventoryDAO;

    @Override
    public WarehouseDTO create(WarehouseDTO dto) {
        Warehouse warehouse = warehouseMapper.toEntity(dto);
        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseDTO getById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return warehouseMapper.toDTO(warehouse);
    }

    @Override
    public List<WarehouseDTO> getAll() {

        return warehouseMapper.toDTOList(warehouseRepository.findAll());
    }

    @Override
    public WarehouseDTO update(Long id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Склад не найден"));
        warehouse.setLocation(dto.getLocation());
        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    // Отчёт по складу
    public List<WarehouseStockDTO> getWarehouseStock(Long warehouseId) {
        return storeProductDAO.findStockByWarehouse(warehouseId);
    }

    @Override
    // Общий отчёт по всем складам
    public List<WarehouseStockDTO> getFullWarehouseStock() {
        return storeProductDAO.findFullStock();
    }

    @Override
    public void deactivate(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Склад с id " + id + " не найден"));

        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void activate(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Склад с id " + id + " не найден"));

        warehouse.setActive(true);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void receiveProduct(Long warehouseId, ProductTransferResponseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Склад с id " + warehouseId + " не найден"));

        Product product = productDAO.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Продукт с id " + dto.getProductId() + " не найден"));

        ProductInventoryLocation warehouseInventory = inventoryDAO
                .findByProductAndLocationTypeAndWarehouseId(product, LocationType.WAREHOUSE, warehouseId)
                .orElseGet(() -> {
                    ProductInventoryLocation newInventory = new ProductInventoryLocation();
                    newInventory.setProduct(product);
                    newInventory.setWarehouseId(warehouseId);
                    newInventory.setLocationType(LocationType.WAREHOUSE);
                    newInventory.setQuantity(0);
                    return newInventory;
                });

        warehouseInventory.setQuantity(warehouseInventory.getQuantity() + dto.getQuantity());

        inventoryDAO.save(warehouseInventory);
    }

}

