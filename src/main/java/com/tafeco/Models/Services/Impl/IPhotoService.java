package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.PhotoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPhotoService {

    PhotoDTO getById(Long id);
    List<PhotoDTO> getAll();
    void deletePhoto(Long id);

    PhotoDTO saveFile(MultipartFile file, Long productId) throws IOException;

}

