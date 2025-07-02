package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStreetDAO extends JpaRepository<Street, Long> {
    Optional<Street> findByNameIgnoreCase(String name);
    Optional<Street> findByName(String name);
    Street save(Street street);
}
