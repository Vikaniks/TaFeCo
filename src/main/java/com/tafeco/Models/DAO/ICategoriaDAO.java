package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Categorise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaDAO extends JpaRepository<Categorise, Integer> {
}
