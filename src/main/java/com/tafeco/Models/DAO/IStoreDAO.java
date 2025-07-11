package com.tafeco.Models.DAO;

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

    @Query("SELECT DISTINCT s FROM Store s JOIN s.storeProducts sp " +
            "WHERE s.warehouse.id = :warehouseId AND sp.currentQuantity > :quantity")
    List<Store> findByWarehouseIdAndCurrentQuantityGreaterThan(
            @Param("warehouseId") Long warehouseId,
            @Param("quantity") int quantity);

    List<Store> findByActiveTrue();

}
