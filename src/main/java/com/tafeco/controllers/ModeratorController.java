package com.tafeco.controllers;

import com.tafeco.DTO.DTO.*;
import com.tafeco.DTO.Mappers.OrderMapper;
import com.tafeco.Models.DAO.ICategoriaDAO;
import com.tafeco.Models.DAO.IDimensionDAO;
import com.tafeco.Models.Entity.Categorise;
import com.tafeco.Models.Entity.Dimension;
import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Services.Impl.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN', 'SUPERADMIN')")
public class ModeratorController {

    private final IProductService productService;
    private final ICategoriseService categoriseService;
    private final IDimensionService dimensionService;
    private final IStoreService storeService;
    private final IStoreProductService storeProductService;
    private final IWarehouseService warehouseService;
    private final ICategoriaDAO categoriseRepository;
    private final IDimensionDAO dimensionRepository;
    private final IOrderService orderService;
    private final IProductInventoryService inventoryService;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<String> checkModeratorAccess() {
        return ResponseEntity.ok("Moderator access granted");
    }

    // Добавить новый продукт
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> create(
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "archive", required = false) List<MultipartFile> archives,
            @RequestPart(value = "photo", required = false) List<MultipartFile> photos
    ) throws IOException {
        ProductDTO created = productService.create(productDTO, archives, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "archive", required = false) List<MultipartFile> archives,
            @RequestPart(value = "photo", required = false) List<MultipartFile> photos
    ) throws IOException {
        ProductDTO updated = productService.update(id, productDTO, archives, photos);
        return ResponseEntity.ok(updated);
    }


    // Добавить новую категорию
    @PostMapping("/categories")
    public ResponseEntity<CategoriaDTO> create(@RequestBody CategoriaDTO categoryDTO) {
        CategoriaDTO created = categoriseService.create(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Добавить единицу измерения
    @PostMapping("/dimensions")
    public ResponseEntity<DimensionDTO> create(@RequestBody DimensionDTO dimensionDTO) {
        DimensionDTO created = dimensionService.create(dimensionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Мягкое удалить продукт по id - сделать не активным
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/force/{id}")
    public ResponseEntity<Void> forceDeleteProduct(@PathVariable Long id) {
        productService.forceDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    // Удалить категорию по id
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
        boolean deleted = categoriseService.deleteCategoria(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // Удалить единицу измерения по id
    @DeleteMapping("/dimensions/{id}")
    public ResponseEntity<?> deleteDimension(@PathVariable Long id) {
        boolean deleted = dimensionService.deleteDimension(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/all")
    public ResponseEntity<List<OrderDTO>> getAllOrdersUnfiltered() {
        List<OrderDTO> orders = orderService.getAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        OrderDTO order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }


    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("status");
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/orders/report/sum")
    public ResponseEntity<OrderSumReportDTO> getSumReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OrderSumReportDTO report = orderService.getSumReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/orders/report")
    public ResponseEntity<?> getReport(
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long storeId
    ) {
        switch (groupBy) {
            case "status":
                return ResponseEntity.ok(orderService.groupByStatus());
            case "period":
                return ResponseEntity.ok(orderService.groupByPeriod(startDate, endDate));
            case "sum":
                return ResponseEntity.ok(orderService.groupBySumRange());
            default:
                return ResponseEntity.ok(orderService.getSummary(status, startDate, endDate, email, productId, warehouseId, storeId));
        }
    }

    @GetMapping("/orders/export")
    public void exportOrdersToCsv(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long storeId,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");

        // Получение заказов
        List<Order> orders = orderService.findOrdersForExport(
                status, startDate, endDate, email, productId, warehouseId, storeId
        );

        // Преобразование в DTO
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .toList();

        // Экспорт в CSV
        orderService.exportToCSV(orderDTOs, response.getWriter());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        ProductDTO dto = productService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // Получить продукт по точному названию
     @GetMapping("/products/by-product")
    public ResponseEntity<ProductDTO> getByProduct(@RequestParam Long product) {
        return ResponseEntity.ok(productService.findByProduct(product));
    }


     // Получить продукты по категории
     @GetMapping("/products/by-category")
    public ResponseEntity<List<ProductDTO>> getByCategory(@RequestParam Long categoryId) {
        Categorise category = categoriseRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        return ResponseEntity.ok(productService.findByCategorise(category));
    }


     // Получить продукты по размерности (dimension)
     @GetMapping("/products/by-dimension")
    public ResponseEntity<List<ProductDTO>> getByDimension(@RequestParam Long dimensionId) {
        Dimension dim = dimensionRepository.findById(dimensionId)
                .orElseThrow(() -> new RuntimeException("Размерность не найдена"));
        return ResponseEntity.ok(productService.findByDimension(dim));
    }

    // Получить продукты по признаку активности (true/false)
    @GetMapping("/products/active/{active}")
    public ResponseEntity<List<ProductDTO>> getByActive(@PathVariable Boolean active) {
        return ResponseEntity.ok(productService.findByActive(active));
    }


    // Отчёт по остаткам товара на точке продаж
    @GetMapping("/products/{productId}/store-report")
    public ResponseEntity<List<ProductStoreReportDTO>> getStoreReportByProduct(@PathVariable Long productId) {
        List<ProductStoreReportDTO> report = storeProductService.getStoreReportByProduct(productId);
        return ResponseEntity.ok(report);
    }

    // Получить отчёт по запасам активных продуктов на точке продаж
    @GetMapping("/products/stock/active")
    public ResponseEntity<List<StoreDTO>> getActiveProductsStore() {
        List<StoreDTO> stockReport = productService.getActiveProductsStore();
        return ResponseEntity.ok(stockReport);
    }

    // Получить отчёт по запасам всех продуктов на складе (с колонкой active)
    @GetMapping("/warehouses/{warehouseId}/stock-report")
    public ResponseEntity<List<WarehouseStockDTO>> getStockReportByWarehouse(@PathVariable Long warehouseId) {
        List<WarehouseStockDTO> report = warehouseService.getWarehouseStock(warehouseId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/stores/{storeId}/stock-report")
    public ResponseEntity<List<WarehouseStockDTO>> getStockReportByStore(@PathVariable Long storeId) {
        List<WarehouseStockDTO> report = storeProductService.getStockByStore(storeId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/stores/stock-report")
    public ResponseEntity<List<WarehouseStockDTO>> getFullStockReport() {
        List<WarehouseStockDTO> report = storeProductService.getFullStock();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/stores/create")
    public ResponseEntity<StoreDTO> create(@RequestBody StoreDTO dto) {
        StoreDTO created = storeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/stores/{id}")
    public ResponseEntity<StoreDTO> update(@PathVariable Long id, @RequestBody StoreDTO dto) {
        return ResponseEntity.ok(storeService.update(id, dto));
    }

    @PatchMapping("/stores/{id}/deactivate")
    public ResponseEntity<Void> deactivateStore(@PathVariable Long id) {
        storeService.deactivateStore(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/stores/activate/{id}")
    public ResponseEntity<?> activateStore(@PathVariable Long id) {
        storeService.activateStore(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/warehouses/create")
    public ResponseEntity<WarehouseDTO> create(@RequestBody WarehouseDTO dto) {
        WarehouseDTO created = warehouseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseDTO> update(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        WarehouseDTO updated = warehouseService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/warehouses/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        warehouseService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/warehouses/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        warehouseService.activate(id);
        return ResponseEntity.ok().build();
    }

    // Поступление товара
    @PostMapping("/warehouses/{id}/receive")
    public ResponseEntity<Void> receiveProduct(@PathVariable Long id, @RequestBody ProductTransferResponseDTO dto) {
        warehouseService.receiveProduct(id, dto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/stores/{id}/receive")
    public ResponseEntity<Void> receiveProductToStore(@PathVariable Long id, @RequestBody ProductTransferResponseDTO dto) {
        storeService.receiveProduct(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/warehouses/{id}/stock")
    public ResponseEntity<List<WarehouseStockDTO>> getStockByWarehouse(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseStock(id));
    }

    @GetMapping("/all/stock")
    public List<WarehouseStockDTO> getFullStock() {
        return warehouseService.getFullWarehouseStock();
    }

    @PostMapping("/warehouses/transfer")
    public ResponseEntity<Void> transferProducts(
            @RequestBody List<ProductTransferRequestDTO> transferRequests) {
        inventoryService.transferProductsToStore(transferRequests);
        return ResponseEntity.ok().build();
    }



}
