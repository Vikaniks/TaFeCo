package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Locality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ILocalityDAO extends JpaRepository<Locality, Long> {
    Optional<Locality> findByNameIgnoreCase(String name);
    Optional<Locality> findByName(String name);
    Locality save(Locality locality);
}
