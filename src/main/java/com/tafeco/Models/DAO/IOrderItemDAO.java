package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderItemDAO extends JpaRepository<OrderItem, Long> {

    boolean existsByProduct_Id(Long id);

}
