package com.tafeco.controllers;

import com.tafeco.DTO.DTO.PhotoDTO;
import com.tafeco.Models.Services.Impl.IPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final IPhotoService photoService;


    @GetMapping("/{id}")
    public PhotoDTO getById(@PathVariable Long id) {
        return photoService.getById(id);
    }

    @GetMapping
    public List<PhotoDTO> getAll() {
        return photoService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        photoService.deletePhoto(id);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDTO> saveFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") Long productId) throws IOException {

        PhotoDTO saved = photoService.saveFile(file, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}

