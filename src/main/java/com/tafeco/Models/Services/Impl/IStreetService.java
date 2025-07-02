package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.Locality;
import com.tafeco.Models.Entity.Street;

import java.util.Optional;

public interface IStreetService {
    Street findOrCreateByName(String name);
    Optional<Street> findByName(String name);
    Street save(Street street);
}
