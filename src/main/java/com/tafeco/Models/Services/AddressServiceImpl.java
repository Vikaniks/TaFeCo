package com.tafeco.Models.Services;

import com.tafeco.Models.DAO.IAddressDAO;
import com.tafeco.Models.Entity.*;
import com.tafeco.Models.Services.Impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final IAddressDAO addressRepository;

    private final IRegionService regionService;
    private final IDistrictService districtService;
    private final ILocalityService localityService;
    private final IStreetService streetService;

    public Address findOrCreateAddress(String regionName, String districtName, String localityName,
                                       String streetName, String house, String apartment, String addressExtra) {

        Region region = regionService.findOrCreateByName(regionName);
        District district = districtService.findOrCreateByName(districtName);
        Locality locality = localityService.findOrCreateByName(localityName);
        Street street = streetService.findOrCreateByName(streetName);

        Optional<Address> optionalAddress = addressRepository.findByRegionAndDistrictAndLocalityAndStreetAndHouseAndApartmentAndAddressExtra(
                region, district, locality, street, house, apartment, addressExtra);

        return optionalAddress.orElseGet(() -> {
            Address newAddress = new Address();
            newAddress.setRegion(region);
            newAddress.setDistrict(district);
            newAddress.setLocality(locality);
            newAddress.setStreet(street);
            newAddress.setHouse(house);
            newAddress.setApartment(apartment);
            newAddress.setAddressExtra(addressExtra);
            return addressRepository.save(newAddress);
        });
    }


}

