package com.tafeco.controllers;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.Models.Services.Impl.ICategoriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
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
    public CategoriaDTO getById(@PathVariable Long id) {
        return categoriseService.getById(id);
    }

    @GetMapping("/all")
    public List<CategoriaDTO> getAll() {
        return categoriseService.getAll();
    }

    @PutMapping("/{id}")
    public CategoriaDTO update(@PathVariable Long id, @RequestBody CategoriaDTO dto) {
        return categoriseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoriseService.deleteCategoria(id);
    }

    @GetMapping("/category")
    public String showCategoryPage(@RequestParam Long id, Model model) {
        model.addAttribute("categoryId", id);
        return "category"; // шаблон category.html
    }



}

