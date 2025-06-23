package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.ArchiveDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IArchiveService {
    ArchiveDTO create(ArchiveDTO dto, MultipartFile file, Long productId) throws IOException;
    ArchiveDTO getById(Long id);
    List<ArchiveDTO> getAll();
    void deleteArchive(Long id);
}
