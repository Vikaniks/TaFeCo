package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.Region;

import java.util.Optional;

public interface IRegionService {
    public Region findOrCreateByName(String name);
    Optional<Region> findByName(String name);
    Region save(Region region);
}
