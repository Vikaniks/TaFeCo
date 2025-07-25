package com.tafeco.controllers;

import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.Models.Services.Impl.IStoreService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final IStoreService storeService;

    @GetMapping("/all")
    public ResponseEntity<List<StoreDTO>> findAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findById(id));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return storeService.delete(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }



    // Получить все товары на складе по ID склада
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StoreDTO>> getStoresByWarehouse(@PathVariable Long warehouseId) {
        List<StoreDTO> stores = storeService.findByWarehouseId(warehouseId);
        return ResponseEntity.ok(stores);
    }

    // Получить товары на складе с количеством больше заданного
    @GetMapping("/warehouse/{warehouseId}/quantity/{quantity}")
    public ResponseEntity<List<StoreDTO>> getStoresByWarehouseAndQuantity(
            @PathVariable Long warehouseId,
            @PathVariable int quantity) {
        List<StoreDTO> stores = storeService.findByWarehouseIdAndCurrentQuantityGreaterThan(warehouseId, quantity);
        return ResponseEntity.ok(stores);
    }

}


