package com.tafeco.Models.DAO;

import com.tafeco.DTO.DTO.ProductStoreReportDTO;
import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStoreProductDAO extends JpaRepository<StoreProduct, Long> {

    List<StoreProduct> findByStoreId(Long storeId);

    List<StoreProduct> findByStoreWarehouseId(Long warehouseId);

    @Query("SELECT new com.tafeco.DTO.DTO.WarehouseStockDTO(" +
            "p.id, p.product, c.type, sp.currentQuantity, p.active) " +
            "FROM StoreProduct sp " +
            "JOIN sp.product p " +
            "JOIN p.categorise c " +
            "WHERE sp.store.warehouse.id = :warehouseId")
    List<WarehouseStockDTO> findStockByWarehouse(@Param("warehouseId") Long warehouseId);

    @Query("SELECT new com.tafeco.DTO.DTO.WarehouseStockDTO(" +
            "p.id, p.product, c.type, sp.currentQuantity, p.active) " +
            "FROM StoreProduct sp " +
            "JOIN sp.product p " +
            "JOIN p.categorise c")
    List<WarehouseStockDTO> findFullStock();

    @Query("SELECT new com.tafeco.DTO.DTO.WarehouseStockDTO(" +
            "p.id, p.product, c.type, sp.currentQuantity, p.active) " +
            "FROM StoreProduct sp " +
            "JOIN sp.product p " +
            "JOIN p.categorise c " +
            "WHERE sp.store.id = :storeId")
    List<WarehouseStockDTO> findStockByStore(@Param("storeId") Long storeId);

    List<StoreProduct> findByProductId(Long productId);

    @Query("SELECT sp FROM StoreProduct sp WHERE sp.store.warehouse.id = :warehouseId AND sp.product.id = :productId")
    Optional<StoreProduct> findByWarehouseIdAndProductId(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);

    @Query("SELECT new com.tafeco.DTO.DTO.ProductStoreReportDTO(" +
            "p.id, p.product, s.id, s.location, sp.currentQuantity) " +
            "FROM StoreProduct sp " +
            "JOIN sp.product p " +
            "JOIN sp.store s " +
            "WHERE p.id = :productId")
    List<ProductStoreReportDTO> getProductStoreReport(@Param("productId") Long productId);

}

