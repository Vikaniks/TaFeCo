package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.District;
import com.tafeco.Models.Entity.Locality;

import java.util.Optional;

public interface IDistrictService {
    public District findOrCreateByName(String name);
    Optional<District> findByName(String name);
    District save(District district);
}
