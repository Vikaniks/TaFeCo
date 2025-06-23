package com.tafeco.controllers;

import com.tafeco.DTO.DTO.ArchiveDTO;
import com.tafeco.Models.Services.Impl.IArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/archives")
@RequiredArgsConstructor
public class ArchiveController {

    private final IArchiveService archiveService;

    @PostMapping("/upload-archive")
    public ArchiveDTO create(
            @RequestPart("dto") ArchiveDTO dto,
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") Long productId
    ) throws IOException {
        return archiveService.create(dto, file, productId);
    }

    // Скачивание архива по имени файла
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadArchive(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads/archives/").resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // Просмотр архива в браузере (если поддерживается, например PDF)
    @GetMapping("/preview/{filename:.+}")
    public ResponseEntity<Resource> previewArchive(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads/archives/").resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    @GetMapping("/{id}")
    public ArchiveDTO getById(@PathVariable Long id) {
        return archiveService.getById(id);
    }

    @GetMapping
    public List<ArchiveDTO> getAll() {
        return archiveService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        archiveService.deleteArchive(id);
    }
}

