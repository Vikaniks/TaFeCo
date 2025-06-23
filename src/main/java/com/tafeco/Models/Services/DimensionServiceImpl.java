package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.DimensionDTO;
import com.tafeco.DTO.Mappers.DimensionMapper;
import com.tafeco.Models.DAO.IDimensionDAO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.Entity.Dimension;
import com.tafeco.Models.Services.Impl.IDimensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DimensionServiceImpl implements IDimensionService {

    private final IDimensionDAO dimensionRepository;
    private final DimensionMapper dimensionMapper;
    private final IProductDAO productRepository;

    @Override
    public DimensionDTO create(DimensionDTO dto) {
        Dimension dimension = dimensionMapper.toEntity(dto);
        return dimensionMapper.toDTO(dimensionRepository.save(dimension));
    }

    @Override
    public DimensionDTO getById(Long id) {
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Единица измерения не найдена"));
        return dimensionMapper.toDTO(dimension);
    }

    @Override
    public List<DimensionDTO> getAll() {
        return dimensionMapper.toDTOList(dimensionRepository.findAll());
    }

    @Override
    public DimensionDTO update(Long id, DimensionDTO dto) {
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dimension not found"));

        dimension.setDimension(dto.getDimension());
        return dimensionMapper.toDTO(dimensionRepository.save(dimension));
    }

    @Override
    public boolean deleteDimension(Long id) {
        Optional<Dimension> dimensionOpt = dimensionRepository.findById(id);
        if (dimensionOpt.isEmpty()) {
            return false;
        }

        Dimension dimension = dimensionOpt.get();

        // Проверка: если есть связанные продукты — удаление запрещено
        if (!productRepository.findByDimension(dimension).isEmpty()) {
            throw new IllegalStateException("Нельзя удалить единицу измерения: она используется в продуктах");
        }

        dimensionRepository.delete(dimension);
        return true;
    }
}

