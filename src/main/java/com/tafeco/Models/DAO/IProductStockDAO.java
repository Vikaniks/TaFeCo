package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductStockDAO extends JpaRepository<ProductStock, Long> {
    Optional<ProductStock> findByProduct_Id(Long productId);
}

