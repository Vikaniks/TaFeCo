package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.ArchiveDTO;
import com.tafeco.DTO.Mappers.ArchiveMapper;
import com.tafeco.Models.DAO.IArchiveDAO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.Entity.Archive;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Services.Impl.IArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveServiceImpl implements IArchiveService {

    private final IArchiveDAO archiveRepository;
    private final IProductDAO productRepository;
    private final ArchiveMapper archiveMapper;

    @Override
    public ArchiveDTO create(ArchiveDTO dto, MultipartFile file, Long productId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));

        // 1. Сохраняем файл на диск
        String uploadDir = "uploads/archives/";
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir + newFilename);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 2. Создаём архив
        Archive archive = new Archive();
        archive.setArchive(newFilename); // сохраняем только имя файла
        archive.setDescription(dto.getDescription());
        archive.setProduct(product);

        Archive saved = archiveRepository.save(archive);
        return archiveMapper.toDTO(saved);
    }


    @Override
    public ArchiveDTO getById(Long id) {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archive not found"));
        return archiveMapper.toDTO(archive);
    }

    @Override
    public List<ArchiveDTO> getAll() {
        return archiveMapper.toDTOList(archiveRepository.findAll());
    }

    @Override
    public void deleteArchive(Long id) {
        archiveRepository.deleteById(id);
    }
}

