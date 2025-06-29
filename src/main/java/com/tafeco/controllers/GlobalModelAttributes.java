package com.tafeco.controllers;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.Models.Services.Impl.ICategoriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ICategoriseService categoriaService;

    @ModelAttribute("categorias")
    public List<CategoriaDTO> populateCategories() {
        return categoriaService.getAll();
    }
}