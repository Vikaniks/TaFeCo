package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.CategoriaDTO;

import java.util.List;

public interface ICategoriseService {
    CategoriaDTO create(CategoriaDTO dto);
    CategoriaDTO getById(Long id);
    List<CategoriaDTO> getAll();
    CategoriaDTO update(Long id, CategoriaDTO dto);
    boolean deleteCategoria(Long id);


}

