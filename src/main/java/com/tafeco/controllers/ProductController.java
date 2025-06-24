package com.tafeco.controllers;

import org.springframework.ui.Model;
import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.Models.Services.Impl.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    // Поиск продуктов по ключевому слову с пагинацией
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDTO>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Page<ProductDTO> result = productService.searchByKeyword(keyword, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products")
    public List<ProductDTO> findAll() {
        return productService.findAll();
    }
}
