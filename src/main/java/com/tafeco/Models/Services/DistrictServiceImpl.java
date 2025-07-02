package com.tafeco.Models.Services;

import com.tafeco.Models.DAO.IDistrictDAO;
import com.tafeco.Models.Entity.District;
import com.tafeco.Models.Entity.Locality;
import com.tafeco.Models.Services.Impl.IDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements IDistrictService {
    private final IDistrictDAO districtRepository;

    @Override
    public District findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        return districtRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    District newDistrict = new District();
                    newDistrict.setName(name.trim());
                    return districtRepository.save(newDistrict);
                });
    }

    @Override
    public Optional<District> findByName(String name) {
        return districtRepository.findByName(name);
    }

    @Override
    public District save(District district) {
        return districtRepository.save(district);
    }
}

