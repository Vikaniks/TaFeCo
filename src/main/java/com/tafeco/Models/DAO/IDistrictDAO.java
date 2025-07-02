package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IDistrictDAO extends JpaRepository<District, Long> {
    Optional<District> findByNameIgnoreCase(String name);
    Optional<District> findByName(String name);
    District save(District district);
}
