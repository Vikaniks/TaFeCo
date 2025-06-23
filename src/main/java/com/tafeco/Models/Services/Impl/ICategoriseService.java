package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.CategoriaDTO;

import java.util.List;

public interface ICategoriseService {
    CategoriaDTO create(CategoriaDTO dto);
    CategoriaDTO getById(int id);
    List<CategoriaDTO> getAll();
    CategoriaDTO update(int id, CategoriaDTO dto);
    boolean deleteCategoria(Integer id);

}

