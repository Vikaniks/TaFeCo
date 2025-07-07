package com.tafeco.controllers;

import com.tafeco.DTO.DTO.CategoriaDTO;
import com.tafeco.DTO.Mappers.CategoriaMapper;
import com.tafeco.Models.Entity.Categorise;
import com.tafeco.Models.Services.Impl.ICategoriseService;
import org.springframework.ui.Model;
import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.Models.Services.Impl.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private  final ICategoriseService categoriseService;
    private final CategoriaMapper categoriaMapper;

    // Поиск продуктов по ключевому слову
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ProductDTO> products = productService.searchByKeyword(keyword.trim());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products")
    public List<ProductDTO> findAll() {
        return productService.findAll();
    }

    @GetMapping("/products/category/{id}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long id) {
        CategoriaDTO categoriaDTO = categoriseService.getById(id);
        if (categoriaDTO == null) {
            return ResponseEntity.notFound().build();
        }

        Categorise categorise = categoriaMapper.toEntity(categoriaDTO);
        List<ProductDTO> products = productService.findByCategorise(categorise);

        return ResponseEntity.ok(products);
    }





}
