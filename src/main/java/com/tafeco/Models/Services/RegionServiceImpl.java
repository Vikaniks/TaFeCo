package com.tafeco.Models.Services;

import com.tafeco.Models.DAO.IRegionDAO;
import com.tafeco.Models.Entity.District;
import com.tafeco.Models.Entity.Region;
import com.tafeco.Models.Services.Impl.IRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements IRegionService {
    private final IRegionDAO regionRepository;


    @Override
    public Region findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        return regionRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    Region newRegion = new Region();
                    newRegion.setName(name.trim());
                    return regionRepository.save(newRegion);
                });
    }

    @Override
    public Optional<Region> findByName(String name) {
        return regionRepository.findByName(name);
    }

    @Override
    public Region save(Region region) {
        return regionRepository.save(region);
    }
}


