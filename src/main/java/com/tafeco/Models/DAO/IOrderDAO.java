package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IOrderDAO extends JpaRepository<Order, Integer> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUser_Id(Long user);

    List<Order> findByUser_IdAndStatusIn(Long user, List<OrderStatus> statuses);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByStatusAndOrderDateBetween(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByUserAndStatusIn(User user, List<OrderStatus> statuses);
}
