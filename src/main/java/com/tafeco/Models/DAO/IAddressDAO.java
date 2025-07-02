package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IAddressDAO extends JpaRepository<Address, Long> {

    // Можно добавить метод для поиска дубликатов адресов по всем полям
    Optional<Address> findByRegionAndDistrictAndLocalityAndStreetAndHouseAndApartmentAndAddressExtra(
            Region region,
            District district,
            Locality locality,
            Street street,
            String house,
            String apartment,
            String addressExtra
    );
    Optional<Address> findByLocality_Name(String name);

}

