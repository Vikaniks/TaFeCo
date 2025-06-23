package com.tafeco.controllers;

import com.tafeco.DTO.DTO.DimensionDTO;
import com.tafeco.Models.Services.Impl.IDimensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dimensions")
@RequiredArgsConstructor
public class DimensionController {

    private final IDimensionService dimensionService;

    @PostMapping
    public DimensionDTO create(@RequestBody DimensionDTO dto) {
        return dimensionService.create(dto);
    }

    @GetMapping("/{id}")
    public DimensionDTO getById(@PathVariable Long id) {
        return dimensionService.getById(id);
    }

    @GetMapping
    public List<DimensionDTO> getAll() {
        return dimensionService.getAll();
    }

    @PutMapping("/{id}")
    public DimensionDTO update(@PathVariable Long id, @RequestBody DimensionDTO dto) {
        return dimensionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dimensionService.deleteDimension(id);
    }
}

