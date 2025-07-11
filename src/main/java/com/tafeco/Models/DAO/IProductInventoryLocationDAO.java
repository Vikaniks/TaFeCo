package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.LocationType;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.ProductInventoryLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IProductInventoryLocationDAO extends JpaRepository<ProductInventoryLocation, Long> {

    Optional<ProductInventoryLocation> findByProductAndLocationTypeAndWarehouseId(
            Product product, LocationType locationType, Long warehouseId);

    Optional<ProductInventoryLocation> findByProductAndLocationTypeAndStoreId(
            Product product, LocationType locationType, Long storeId);

    List<ProductInventoryLocation> findByWarehouseId(Long warehouseId);

    List<ProductInventoryLocation> findByStoreId(Long storeId);



}
