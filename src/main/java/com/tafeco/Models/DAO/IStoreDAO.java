package com.tafeco.Models.DAO;

import com.tafeco.DTO.DTO.WarehouseStockDTO;
import com.tafeco.Models.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStoreDAO extends JpaRepository<Store, Long> {

    List<Store> findByWarehouseId(Long warehouseId);
    List<Store> findByWarehouseIdAndCurrentQuantityGreaterThan(Long warehouseId, int quantity);

    List<Store> findByProductId(Long productId);

    Optional<Store> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Query("SELECT new com.tafeco.DTO.DTO.WarehouseStockDTO(" +
            "p.id, p.product, c.type, s.currentQuantity, p.active) " +
            "FROM Store s " +
            "JOIN s.product p " +
            "JOIN p.categorise c " +
            "WHERE s.warehouse.id = :warehouseId")
    List<WarehouseStockDTO> findStockByWarehouse(@Param("warehouseId") Long warehouseId);

    // если нужен отчёт по всем складам без фильтра
    @Query("SELECT new com.tafeco.DTO.DTO.WarehouseStockDTO(" +
            "p.id, p.product, c.type, s.currentQuantity, p.active) " +
            "FROM Store s " +
            "JOIN s.product p " +
            "JOIN p.categorise c")
    List<WarehouseStockDTO> findFullStock();

}

