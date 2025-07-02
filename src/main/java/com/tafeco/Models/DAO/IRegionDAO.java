package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRegionDAO extends JpaRepository<Region, Long> {
    Optional<Region> findByNameIgnoreCase(String name);
    Optional<Region> findByName(String name);
    Region save(Region region);
}

