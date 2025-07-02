package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.Address;

import java.util.Optional;

public interface IAddressService {
    public Address findOrCreateAddress(String regionName, String districtName, String localityName,
                                       String streetName, String house, String apartment, String addressExtra);

}
