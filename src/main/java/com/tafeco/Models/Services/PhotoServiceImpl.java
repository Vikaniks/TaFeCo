package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.PhotoDTO;
import com.tafeco.DTO.Mappers.PhotoMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.IPhotoDAO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.Entity.Photo;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Services.Impl.IPhotoService;
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
public class PhotoServiceImpl implements IPhotoService {

    private final IPhotoDAO photoRepository;
    private final IProductDAO productRepository;
    private final PhotoMapper photoMapper;


    @Override
    public PhotoDTO getById(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Фото не найдено"));
        return photoMapper.toDTO(photo);
    }

    @Override
    public List<PhotoDTO> getAll() {
        return photoMapper.toDTOList(photoRepository.findAll());
    }

    @Override
    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }

    

    @Override
    public PhotoDTO saveFile(MultipartFile file, Long productId) throws IOException {
        // 1. Создание объекта Photo и сохранение в БД
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден"));
        // 2. Генерация имени и путь
        String uploadDir = "uploads/photos/";
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir + newFilename);

        // 3. Создание папки (если нет) и сохранение файла
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        // Сохраняем
        Photo photo = new Photo();
        photo.setPhoto(newFilename); // сохраняем только имя, а не путь
        photo.setProduct(product);

        Photo saved = photoRepository.save(photo);

        return photoMapper.toDTO(photoRepository.save(photo));
    }

}

