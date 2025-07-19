package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductDAO extends JpaRepository<Product, Long> {

    // Поиск по активности
    List<Product> findByActive(Boolean active);

    // Поиск по названию продукта (точное совпадение)
    Product findByProduct(String product);

    // Поиск по части названия (нечёткий поиск)
    @Query("SELECT p FROM Product p WHERE LOWER(p.product) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByProductContainingIgnoreCase(@Param("keyword") String keyword);

    // Поиск по категории
    List<Product> findByCategorise(Categorise categorise);

    // Поиск по размерности (единице измерения)
    List<Product> findByDimension(Dimension dimension);

    Optional<Product> findById(Long id);


}