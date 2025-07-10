package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOrderDAO extends JpaRepository<Order, Integer> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUser_Id(Long user);

    List<Order> findByUser_IdAndStatusIn(Long user, List<OrderStatus> statuses);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByStatusAndOrderDateBetween(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByUserAndStatusIn(User user, List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    long countByOrderDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
        SELECT SUM(o.totalPrice)
        FROM Order o
        WHERE (:startDate IS NULL OR o.orderDate >= :startDate)
          AND (:endDate IS NULL OR o.orderDate <= :endDate)
    """)
    BigDecimal sumTotalPriceByDateBetween(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT o.status, COUNT(o)
        FROM Order o
        WHERE (:startDate IS NULL OR o.orderDate >= :startDate)
          AND (:endDate IS NULL OR o.orderDate <= :endDate)
        GROUP BY o.status
    """)
    List<Object[]> countGroupedByStatusRaw(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);


    @Query("""
        SELECT o FROM Order o
        JOIN o.user u
        WHERE (:status IS NULL OR o.status = :status)
          AND (:startDate IS NULL OR o.orderDate >= :startDate)
          AND (:endDate IS NULL OR o.orderDate <= :endDate)
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    Page<Order> findFilteredOrders(@Param("status") OrderStatus status,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("email") String email,
                                   Pageable pageable);

    @Query("""
        SELECT COUNT(o) FROM Order o
        JOIN o.user u
        WHERE (:status IS NULL OR o.status = :status)
          AND (:startDate IS NULL OR o.orderDate >= :startDate)
          AND (:endDate IS NULL OR o.orderDate <= :endDate)
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    long countFilteredOrders(@Param("status") OrderStatus status,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate,
                             @Param("email") String email);



    @Query("""
SELECT DISTINCT o FROM Order o
JOIN o.user u
JOIN o.items i
JOIN i.product p
JOIN p.stores sp
JOIN sp.store s
JOIN s.warehouse w
WHERE (:status IS NULL OR o.status = :status)
  AND (:startDate IS NULL OR o.orderDate >= :startDate)
  AND (:endDate IS NULL OR o.orderDate <= :endDate)
  AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
  AND (:productId IS NULL OR p.id = :productId)
  AND (:warehouseId IS NULL OR w.id = :warehouseId)
  AND (:storeId IS NULL OR s.id = :storeId)
""")
    List<Order> findFilteredForExport(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("email") String email,
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("storeId") Long storeId
    );


    @Query("""
SELECT DISTINCT o FROM Order o
JOIN o.user u
JOIN o.items i
JOIN i.product p
JOIN p.stores sp
JOIN sp.store s
JOIN s.warehouse w
WHERE (:status IS NULL OR o.status = :status)
  AND (:startDate IS NULL OR o.orderDate >= :startDate)
  AND (:endDate IS NULL OR o.orderDate <= :endDate)
  AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
  AND (:productId IS NULL OR p.id = :productId)
  AND (:warehouseId IS NULL OR w.id = :warehouseId)
  AND (:storeId IS NULL OR s.id = :storeId)
""")
    Page<Order> findFilteredFull(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("email") String email,
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("storeId") Long storeId,
            Pageable pageable
    );

    @Query(value = """
    SELECT order_data, COUNT(*), SUM(total_price)
    FROM orders
    WHERE (:startDate IS NULL OR order_data >= :startDate)
      AND (:endDate IS NULL OR order_data <= :endDate)
    GROUP BY order_data
    ORDER BY order_data
""", nativeQuery = true)
    List<Object[]> groupByDateWithSumAndCount(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);


    long countByTotalPriceBetween(BigDecimal start, BigDecimal end);

    long countByTotalPriceGreaterThanEqual(BigDecimal start);


}
