package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderItemDAO extends JpaRepository<OrderItem, Long> {
}
