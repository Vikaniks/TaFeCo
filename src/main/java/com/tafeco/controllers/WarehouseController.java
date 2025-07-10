package com.tafeco.controllers;

import com.tafeco.DTO.DTO.WarehouseDTO;
import com.tafeco.Models.Services.Impl.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;

    @PostMapping
    public WarehouseDTO create(@RequestBody WarehouseDTO dto) {
        return warehouseService.create(dto);
    }

    @GetMapping("/{id}")
    public WarehouseDTO getById(@PathVariable Long id) {
        return warehouseService.getById(id);
    }

    @GetMapping("/all")
    public List<WarehouseDTO> getAll() {
        return warehouseService.getAll();
    }

    @PutMapping("/{id}")
    public WarehouseDTO update(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        return warehouseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        warehouseService.delete(id);
    }
}

