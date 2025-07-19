package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.ArchiveDTO;
import com.tafeco.DTO.DTO.PhotoDTO;
import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.DTO.Mappers.ProductMapper;
import com.tafeco.DTO.Mappers.StoreMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.*;
import com.tafeco.Models.Entity.*;
import com.tafeco.Models.Services.Impl.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final IProductDAO productRepository;
    private final ICategoriaDAO categoriseRepository;
    private final IDimensionDAO dimensionRepository;
    private final IPhotoDAO photoRepository;
    private final IArchiveDAO archiveRepository;
    private final ProductMapper productMapper;
    private final IOrderItemDAO orderItemRepository;


    @Override
    public ProductDTO create(ProductDTO productDto, List<MultipartFile> archives, List<MultipartFile> photos) throws IOException {
        Product product = new Product();
        product.setProduct(productDto.getProduct());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        if (productDto.getCategorise() != null) {
            categoriseRepository.findById(productDto.getCategorise()).ifPresent(product::setCategorise);
        }

        if (productDto.getDimension() != null) {
            dimensionRepository.findById(productDto.getDimension()).ifPresent(product::setDimension);
        }

        // Сохраняем продукт без фото/архивов, чтобы получить ID
        Product saved = productRepository.save(product);

        if (photos != null) {
            Set<Photo> photoEntities = new HashSet<>();
            for (MultipartFile photoFile : photos) {
                String path = saveFile(photoFile, "uploads/photos/");
                Photo photo = new Photo();
                photo.setPhoto(path);
                photo.setProduct(saved);
                photoEntities.add(photoRepository.save(photo));
            }
            saved.setPhotos(photoEntities);
        }

        if (archives != null) {
            Set<Archive> archiveEntities = new HashSet<>();
            for (MultipartFile archiveFile : archives) {
                String path = saveFile(archiveFile, "uploads/archives/");
                Archive archive = new Archive();
                archive.setArchive(path);
                archive.setDescription("Файл: " + archiveFile.getOriginalFilename());
                archive.setProduct(saved);
                archiveEntities.add(archiveRepository.save(archive));
            }
            saved.setArchives(archiveEntities);
        }

        log.info("Продукт создан: ID {}", saved.getId());
        return productMapper.toDTO(saved);
    }


    @Override
    public ProductDTO update(Long id, ProductDTO dto, List<MultipartFile> archives, List<MultipartFile> photos) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден: " + id));

        productMapper.updateEntityFromDto(dto, existing);

        if (dto.getCategorise() != null) {
            categoriseRepository.findById(dto.getCategorise()).ifPresent(existing::setCategorise);
        }

        if (dto.getDimension() != null) {
            dimensionRepository.findById(dto.getDimension()).ifPresent(existing::setDimension);
        }

        if (photos != null) {
            for (MultipartFile photoFile : photos) {
                String path = saveFile(photoFile, "uploads/photos/");
                Photo photo = new Photo();
                photo.setPhoto(path);
                photo.setProduct(existing);
                photoRepository.save(photo);
            }
        }

        if (archives != null) {
            for (MultipartFile archiveFile : archives) {
                String path = saveFile(archiveFile, "uploads/archives/");
                Archive archive = new Archive();
                archive.setArchive(path);
                archive.setDescription("Файл: " + archiveFile.getOriginalFilename());
                archive.setProduct(existing);
                archiveRepository.save(archive);
            }
        }

        Product updated = productRepository.save(existing);
        log.info("Продукт обновлён: ID {}", id);
        return productMapper.toDTO(updated);
    }


    private String saveFile(MultipartFile file, String dir) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(dir + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return path.toString();
    }


    @Override
    @Transactional
    public void deleteProduct(Long id) {
        boolean isUsed = orderItemRepository.existsByProduct_Id(id);
        System.out.println("Product used in orders: " + isUsed);

        if (isUsed) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product is used in orders");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));

        System.out.println("Before setActive: " + product.getActive());
        product.setActive(false);
        System.out.println("After setActive: " + product.getActive());

        productRepository.save(product);
        productRepository.flush();
    }

    @Override
    @Transactional
    public void forceDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));

        // Убираем связи с внешними сущностями
        product.setCategorise(null);
        product.setDimension(null);

        productRepository.save(product);

        // Удаляем связанные OrderItem
        List<OrderItem> relatedItems = orderItemRepository.findByProductId(id);
        orderItemRepository.deleteAll(relatedItems);

        productRepository.delete(product);
    }



    @Override
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден: " + id));
    }

    @Override
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            log.info("Список продуктов пуст.");
            return Collections.emptyList(); // возвращает пустой список, но не null
        }

        return products.stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @Override
    public List<ProductDTO> findByActive(Boolean active) {
        List<Product> products = productRepository.findByActive(active);

        if (products.isEmpty()) {
            log.info("Продукты в наличии не найдены. (active={})", active);
            return Collections.emptyList();
        }

        return products.stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @Override
    public ProductDTO findByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт с ID " + productId + " не найден"));
        return productMapper.toDTO(product);
    }


    @Override
    public List<ProductDTO> findByCategorise(Categorise categorise) {
        List<Product> products = productRepository.findByCategorise(categorise);

        if (products.isEmpty()) {
            log.info("Продукты с категорией {} не найдены", categorise.getId());
            return Collections.emptyList();
        }

        return products.stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @Override
    public List<ProductDTO> findByDimension(Dimension dimension) {
        List<Product> products = productRepository.findByDimension(dimension);

        if (products.isEmpty()) {
            log.info("Продукты с единицей измерения {} не найдены", dimension.getId());
            return Collections.emptyList();
        }

        return products.stream()
                .map(productMapper::toDTO)
                .toList();
    }


    public List<ProductDTO> searchByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductContainingIgnoreCase(keyword);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreDTO> getActiveProductsStore() {
        // Получаем все активные продукты
        List<Product> activeProducts = productRepository.findByActive(true);

        List<StoreDTO> result = new ArrayList<>();

        for (Product product : activeProducts) {
            for (StoreProduct sp : product.getStores()) {
                StoreDTO dto = new StoreDTO();
                dto.setId(sp.getId());
                dto.setCurrentQuantity(sp.getCurrentQuantity());
                dto.setProduct(product.getId());


                dto.setStoreName(sp.getStore().getStoreName());
                dto.setLocation(sp.getStore().getLocation());

                result.add(dto);
            }
        }

        return result;

    }

}


