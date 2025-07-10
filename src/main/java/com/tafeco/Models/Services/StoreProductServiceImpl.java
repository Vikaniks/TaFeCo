package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.CreateStoreProductDTO;
import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.StoreProductDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.DTO.Mappers.StoreProductMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.DAO.IStoreDAO;
import com.tafeco.Models.DAO.IStoreProductDAO;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.StoreProduct;
import com.tafeco.Models.Services.Impl.IStoreProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreProductServiceImpl implements IStoreProductService {

        private final IStoreProductDAO storeProductRepository;
        private final IProductDAO productRepository;
        private final IStoreDAO storeRepository;
        private final StoreProductMapper storeProductMapper;

        @Override
        public StoreProductDTO create(CreateStoreProductDTO dto) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            Store store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

            StoreProduct storeProduct = storeProductMapper.toEntity(dto, product, store);
            return storeProductMapper.toDTO(storeProductRepository.save(storeProduct));
        }

    @Override
    public StoreProductDTO update(Long id, StoreProductDTO dto) {
        StoreProduct existing = storeProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StoreProduct not found"));

        // Обновляем количество
        existing.setCurrentQuantity(dto.getCurrentQuantity());
        existing.setMaxQuantity(dto.getMaxQuantity());

        // Проверяем и обновляем продукт, если изменился
        if (!existing.getProduct().getId().equals(dto.getProduct())) {
            Product product = productRepository.findById(dto.getProduct())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            existing.setProduct(product);
        }

        // Проверяем и обновляем магазин (store), если изменился склад или локация
        if (!existing.getStore().getWarehouse().getId().equals(dto.getWarehouse())
                || !existing.getStore().getWarehouse().getLocation().equals(dto.getWarehouseLocation())) {

            // Получаем все магазины по id склада
            List<Store> stores = storeRepository.findByWarehouseId(dto.getWarehouse());

            if (stores.isEmpty()) {
                throw new ResourceNotFoundException("Stores not found by warehouse id " + dto.getWarehouse());
            }

            // Ищем магазин с нужной локацией склада
            Store newStore = stores.stream()
                    .filter(store -> store.getWarehouse() != null
                            && dto.getWarehouseLocation().equals(store.getWarehouse().getLocation()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Store with warehouse location '" + dto.getWarehouseLocation() + "' not found"));

            existing.setStore(newStore);
        }

        // Сохраняем и возвращаем DTO
        return storeProductMapper.toDTO(storeProductRepository.save(existing));
    }

    @Override
        public boolean delete(Long id) {
            if (!storeProductRepository.existsById(id)) return false;
            storeProductRepository.deleteById(id);
            return true;
        }

        @Override
        public List<WarehouseStockDTO> getFullStock() {
            return storeProductRepository.findFullStock();
        }

        @Override
        public List<WarehouseStockDTO> getStockByStore(Long storeId) {
            List<StoreProduct> entries = storeProductRepository.findByStoreId(storeId);

            return entries.stream().map(sp -> new WarehouseStockDTO(
                    sp.getProduct().getId(),
                    sp.getProduct().getProduct(),
                    sp.getProduct().getCategorise().getType(),
                    sp.getCurrentQuantity(),
                    sp.getProduct().isActive()
            )).toList();
        }

        @Override
        public List<ProductStoreReportDTO> getStoreReportByProduct(Long productId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            List<StoreProduct> entries = storeProductRepository.findByProductId(productId);

            return entries.stream().map(sp -> new ProductStoreReportDTO(
                    product.getId(),
                    product.getProduct(),
                    sp.getStore().getId(),
                    sp.getStore().getLocation(),
                    sp.getCurrentQuantity()
            )).toList();
        }


}
