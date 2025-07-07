package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.Models.Entity.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

public interface IProductService {

    ProductDTO create(ProductDTO productDto, List<MultipartFile> archives, List<MultipartFile> photos) throws IOException;

    ProductDTO update(Long id, ProductDTO productDto, List<MultipartFile> archives, List<MultipartFile> photos) throws IOException;

    boolean deleteProduct(Long id);
    ProductDTO findById(Long id);
    List<ProductDTO> findAll();

    // методы по фильтрации — также возвращают DTO
    List<ProductDTO> findByActive(Boolean active);
    ProductDTO findByProduct(Long product);

    // методы работы со складами

    List<ProductDTO> findByCategorise(Categorise categorise);
    List<ProductDTO> findByDimension(Dimension dimension);


    List<ProductDTO> searchByKeyword(String keyword);
    List<StoreDTO> getActiveProductsStore();
}
