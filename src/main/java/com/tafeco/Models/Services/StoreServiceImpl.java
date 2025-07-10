package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.DTO.Mappers.StoreMapper;
import com.tafeco.Models.DAO.IStoreDAO;
import com.tafeco.Models.DAO.IStoreProductDAO;
import com.tafeco.Models.DAO.IWarehouseDAO;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.Warehouse;
import com.tafeco.Models.Services.Impl.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements IStoreService {

    private final IStoreDAO storeRepository;
    private final StoreMapper storeMapper;
    private final IStoreProductDAO storeProductDAO;
    private final IWarehouseDAO warehouseRepository;

    @Override
    public StoreDTO create(StoreDTO storeDTO) {
        Store store = storeMapper.toEntity(storeDTO);

        // Подгружаем warehouse по id из DTO и устанавливаем в entity
        Warehouse warehouse = warehouseRepository.findById(storeDTO.getWarehouse())
                .orElseThrow(() -> new RuntimeException("Warehouse not found, id: " + storeDTO.getWarehouse()));
        store.setWarehouse(warehouse);

        Store saved = storeRepository.save(store);
        return storeMapper.toDTO(saved);
    }

    @Override
    public StoreDTO update(Long id, StoreDTO storeDTO) {
        Store existing = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found, id: " + id));

        // Маппим поля из DTO в существующий entity, кроме warehouse (его надо отдельно)
        storeMapper.toEntity(storeDTO); // Создаётся новая entity, но её не сохраняем
        // Лучше вызвать update вручную, потому что маппер не умеет обновлять существующий объект
        existing.setStoreName(storeDTO.getStoreName());
        existing.setLocation(storeDTO.getLocation());

        if (storeDTO.getWarehouse() != null &&
                (existing.getWarehouse() == null || !existing.getWarehouse().getId().equals(storeDTO.getWarehouse()))) {

            Warehouse warehouse = warehouseRepository.findById(storeDTO.getWarehouse())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found, id: " + storeDTO.getWarehouse()));
            existing.setWarehouse(warehouse);
        }

        Store updated = storeRepository.save(existing);
        return storeMapper.toDTO(updated);
    }

    @Override
    public boolean delete(Long id) {
        if (!storeRepository.existsById(id)) {
            return false;
        }
        storeRepository.deleteById(id);
        return true;
    }

    @Override
    public StoreDTO findById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found, id: " + id));
        return storeMapper.toDTO(store);
    }

    @Override
    public List<StoreDTO> findAll() {
        List<Store> stores = storeRepository.findAll();
        return storeMapper.toDTOList(stores);
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
    public List<ProductStoreReportDTO> getProductStoreReport(Long productId) {
        return storeProductDAO.getProductStoreReport(productId);
    }

    @Override
    public List<WarehouseStockDTO> getStockByStore(Long storeId) {
        return storeProductDAO.findStockByStore(storeId);
    }

    @Override
    public List<WarehouseStockDTO> getFullStockForAllStores() {
        return storeProductDAO.findFullStock();
    }


}
