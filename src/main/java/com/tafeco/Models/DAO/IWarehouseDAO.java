package com.tafeco.Models.DAO;


import com.tafeco.Models.Entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWarehouseDAO extends JpaRepository<Warehouse, Long> {

}
