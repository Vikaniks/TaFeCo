package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.Locality;

import java.util.Optional;

public interface ILocalityService {
    public Locality findOrCreateByName(String name);
    Optional<Locality> findByName(String name);
    Locality save(Locality locality);
}
