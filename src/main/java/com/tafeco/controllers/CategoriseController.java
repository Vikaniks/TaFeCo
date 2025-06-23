package com.tafeco.controllers;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.Models.Services.Impl.ICategoriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriseController {

    private final ICategoriseService categoriseService;

    @PostMapping
    public CategoriaDTO create(@RequestBody CategoriaDTO dto) {
        return categoriseService.create(dto);
    }

    @GetMapping("/{id}")
    public CategoriaDTO getById(@PathVariable int id) {
        return categoriseService.getById(id);
    }

    @GetMapping
    public List<CategoriaDTO> getAll() {
        return categoriseService.getAll();
    }

    @PutMapping("/{id}")
    public CategoriaDTO update(@PathVariable int id, @RequestBody CategoriaDTO dto) {
        return categoriseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        categoriseService.deleteCategoria(id);
    }
}

