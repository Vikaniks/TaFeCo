package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.DTO.Mappers.CategoriaMapper;
import com.tafeco.Models.DAO.ICategoriaDAO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.Entity.Categorise;
import com.tafeco.Models.Services.Impl.ICategoriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriseServiceImpl implements ICategoriseService {

    private final ICategoriaDAO categoriseRepository;
    private final CategoriaMapper categoriseMapper;
    private final IProductDAO productRepository;

    @Override
    public CategoriaDTO create(CategoriaDTO dto) {
        Categorise categorise = categoriseMapper.toEntity(dto);
        return categoriseMapper.toDTO(categoriseRepository.save(categorise));
    }

    @Override
    public CategoriaDTO getById(Long id) {
        Categorise categorise = categoriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoriseMapper.toDTO(categorise);
    }

    @Override
    public List<CategoriaDTO> getAll() {
        return categoriseMapper.toDTOList(categoriseRepository.findAll());
    }

    @Override
    public CategoriaDTO update(Long id, CategoriaDTO dto) {
        Categorise categorise = categoriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categorise.setType(dto.getType());
        return categoriseMapper.toDTO(categoriseRepository.save(categorise));
    }

    @Override
    public boolean deleteCategoria(Long id) {
        Optional<Categorise> categoryOpt = categoriseRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            return false;
        }

        Categorise category = categoryOpt.get();

        // Проверка: если есть связанные продукты — запрещаем удаление
        if (!productRepository.findByCategorise(category).isEmpty()) {
            throw new IllegalStateException("Нельзя удалить категорию: к ней привязаны продукты");
        }

        categoriseRepository.delete(category);
        return true;
    }



}

