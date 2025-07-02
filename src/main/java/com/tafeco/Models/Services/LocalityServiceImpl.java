package com.tafeco.Models.Services;

import com.tafeco.Models.DAO.ILocalityDAO;
import com.tafeco.Models.Entity.Locality;
import com.tafeco.Models.Services.Impl.ILocalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalityServiceImpl implements ILocalityService {
    private final ILocalityDAO localityRepository;

    @Override
    public Locality findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        return localityRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    Locality newLocality = new Locality();
                    newLocality.setName(name.trim());
                    return localityRepository.save(newLocality);
                });
    }
    @Override
    public Optional<Locality> findByName(String name) {
        return localityRepository.findByName(name);
    }

    @Override
    public Locality save(Locality locality) {
        return localityRepository.save(locality);
    }
}