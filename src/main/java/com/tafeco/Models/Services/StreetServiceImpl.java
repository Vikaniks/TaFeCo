package com.tafeco.Models.Services;

import com.tafeco.Models.DAO.IStreetDAO;
import com.tafeco.Models.Entity.District;
import com.tafeco.Models.Entity.Street;
import com.tafeco.Models.Services.Impl.IStreetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreetServiceImpl implements IStreetService {
    private final IStreetDAO streetRepository;

    @Override
    public Street findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        return streetRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    Street newStreet = new Street();
                    newStreet.setName(name.trim());
                    return streetRepository.save(newStreet);
                });
    }

    @Override
    public Optional<Street> findByName(String name) {
        return streetRepository.findByName(name);
    }

    @Override
    public Street save(Street street) {
        return streetRepository.save(street);
    }
}
