package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDimensionDAO extends JpaRepository<Dimension, Long> {
}
