package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStoreDAO extends JpaRepository<Store, Long> {

    List<Store> findByWarehouseId(Long warehouseId);
    List<Store> findByWarehouseIdAndCurrentQuantityGreaterThan(Long warehouseId, int quantity);

    List<Store> findByProductId(Long productId);

    Optional<Store> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}

