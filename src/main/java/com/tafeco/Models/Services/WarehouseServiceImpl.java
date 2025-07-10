package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.WarehouseDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.DTO.Mappers.WarehouseMapper;
import com.tafeco.Models.DAO.IStoreDAO;
import com.tafeco.Models.DAO.IStoreProductDAO;
import com.tafeco.Models.DAO.IWarehouseDAO;
import com.tafeco.Models.Entity.Warehouse;
import com.tafeco.Models.Services.Impl.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements IWarehouseService {

    private final IWarehouseDAO warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final IStoreDAO storeDAO;
    private final IStoreProductDAO storeProductDAO;

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

}

