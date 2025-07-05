package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.OrderDetailDTO;
import com.tafeco.DTO.DTO.OrderSummaryDTO;
import com.tafeco.DTO.Mappers.OrderDetailMapper;
import com.tafeco.DTO.Mappers.OrderMapper;
import com.tafeco.Models.DAO.IOrderDAO;
import com.tafeco.Models.DAO.IProductDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.*;
import com.tafeco.Models.Services.Impl.INotificationService;
import com.tafeco.Models.Services.Impl.IOrderService;
import com.tafeco.Models.Services.Impl.IProductStockService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderDAO orderRepository;
    private final IUserDAO userRepository;
    private final IProductDAO productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final INotificationService notificationService;
    private static final Long DEFAULT_WAREHOUSE_ID = 1L;
    private final IProductStockService productStockService;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO dto, String email) {

        Order order = new Order();

        // 1. Пользователь: авторизованный или null (гость)
        User user = null;

        System.out.println("DTO.getUser() = " + dto.getUser());
        System.out.println("email = " + email);

        if (dto.getUser() != null) {
            user = userRepository.findById(dto.getUser())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        } else if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        }
        System.out.println("User for order: " + (user != null ? user.getId() : "null"));
        order.setUser(user); // может быть null для гостя

        // 2. Дата заказа — используем из DTO, если есть, иначе — текущая
        if (dto.getOrderDate() != null) {
            order.setOrderDate(dto.getOrderDate());
        } else {
            order.setOrderDate(LocalDate.now());
        }

        // 3. Статус заказа — из DTO или NEW
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.NEW);

        // 4. Позиции заказа
        Set<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    // Ищем продукт по ID
                    Product product = productRepository.findById(itemDto.getProduct())
                            .orElseThrow(() -> new RuntimeException("Продукт не найден: ID = " + itemDto.getProduct()));

                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setQuantity(itemDto.getQuantity());
                    item.setPriceAtOrderTime(itemDto.getPriceAtOrderTime()); // уже передаётся с фронта
                    item.setOrder(order); // устанавливаем связь
                    return item;
                })
                .collect(Collectors.toSet());
        order.setItems(items);

        // 5. Общая цена — пересчитываем (на всякий случай)
        BigDecimal total = items.stream()
                .map(i -> i.getPriceAtOrderTime().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        // 6. Сохраняем заказ (каскадно сохранятся и позиции)
        Order savedOrder = orderRepository.save(order);

        OrderDTO result = orderMapper.toDTO(savedOrder);


        // 7. Отправка уведомления менеджеру
        notificationService.notifyManager(savedOrder);

        // 8. Возвращаем OrderDTO
        return result;

    }

    @Override
    public OrderDTO getById(int id) {
        return orderMapper.toDTO(orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден")));
    }

    @Override
    public List<OrderDTO> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDTO> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getByUserId(Long user) {
        return orderRepository.findByUser_Id(user).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getByUserIdAndStatuses(Long user, List<OrderStatus> statuses) {
        return orderRepository.findByUser_IdAndStatusIn(user, statuses).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailDTO getOrderDetailById(int order) {
        Order orderDetails = orderRepository.findById(order)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        return orderDetailMapper.toDTO(orderDetails);
    }

    @Override
    public Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Order> orders;

        if (status != null && startDate != null && endDate != null) {
            orders = orderRepository.findByStatusAndOrderDateBetween(status, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(orderMapper::toDTO);
    }

    @Override
    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderId));

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }

        order.setStatus(status);
        orderRepository.save(order);
    }


    @Transactional
    @Override
    public OrderDTO changeOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id " + orderId
                ));

        if (!isValidStatusChange(order.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    String.format(
                            "Cannot change order status from %s to %s",
                            order.getStatus(), newStatus
                    )
            );
        }

        order.setStatus(newStatus);

        switch (newStatus) {
            case SHIPPED:
                decreaseProductStock(order); // Списать со склада
                break;
            case CANCELLED:
                returnProductStock(order);   // Вернуть на склад
                break;
            default:
                // Других побочных эффектов нет
                break;
        }

        Order savedOrder = orderRepository.save(order);

        notificationService.notifyUser(
                savedOrder.getUser().getEmail(),
                "Your order #" + savedOrder.getId() +
                        " status changed to " + newStatus.name()
        );

        return orderMapper.toDTO(savedOrder);
    }

    // --- Валидация переходов
    private boolean isValidStatusChange(OrderStatus current, OrderStatus next) {
        switch (current) {
            case NEW:
                return next == OrderStatus.SHIPPED
                        || next == OrderStatus.CANCELLED
                        || next == OrderStatus.NEW;
            case SHIPPED:
                return next == OrderStatus.DELIVERED
                        || next == OrderStatus.CANCELLED
                        || next == OrderStatus.SHIPPED;
            case DELIVERED:
            case CANCELLED:
            default:
                return false;
        }
    }

    // --- Списание со склада
    private void decreaseProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productStockService.decreaseStock(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    DEFAULT_WAREHOUSE_ID
            );
        }
    }

    // --- Возврат на склад
    private void returnProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productStockService.increaseStock(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    DEFAULT_WAREHOUSE_ID
            );
        }
    }

    @Override
    public OrderSummaryDTO getSummary(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String email,
            Long productId,
            Long warehouseId,
            Long storeId
    ) {
        // Получаем все заказы, удовлетворяющие фильтру, без пагинации
        List<Order> filteredOrders = orderRepository.findFilteredForExport(
                status, startDate, endDate, email, productId, warehouseId, storeId
        );

        // Суммарная выручка
        BigDecimal totalRevenue = filteredOrders.stream()
                .map(Order::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Подсчёт количества заказов по статусам
        Map<OrderStatus, Long> ordersByStatus = filteredOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // Формируем результат
        OrderSummaryDTO summary = new OrderSummaryDTO();
        summary.setTotalOrders(filteredOrders.size());
        summary.setTotalRevenue(totalRevenue);
        summary.setOrdersByStatus(ordersByStatus);

        return summary;
    }

    @Override
    public Page<OrderDTO> findOrders(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String email,
            Long productId,
            Long warehouseId,
            Long storeId,
            Pageable pageable
    ) {
        Page<Order> orders = orderRepository.findFilteredFull(
                status,
                startDate,
                endDate,
                email,
                productId,
                warehouseId,
                storeId,
                pageable
        );

        return orders.map(orderMapper::toDTO);
    }



    @Override
    public void exportToCSV(List<OrderDTO> orders, Writer writer) {
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Дата", "Статус", "Email", "Общая сумма"))) {

            for (OrderDTO order : orders) {
                csvPrinter.printRecord(
                        order.getId(),
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getUser(),
                        order.getTotalPrice()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте заказов в CSV", e);
        }
    }


    @Override
    public List<Order> findOrdersForExport(OrderStatus status, LocalDate startDate, LocalDate endDate, String email, Long productId, Long warehouseId, Long storeId) {
        return List.of();
    }


    @Override
    public List<OrderDTO> findOrdersWithoutPagination(OrderStatus status, LocalDate startDate, LocalDate endDate, String email, Long productId, Long warehouseId, Long storeId) {
        return List.of();
    }

    @Override
    public BigDecimal calculateRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = orderRepository.sumTotalPriceByDateBetween(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public Map<OrderStatus, Long> countGroupedByStatus(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepository.countGroupedByStatusRaw(startDate, endDate);
        return results.stream()
                .collect(Collectors.toMap(
                        r -> (OrderStatus) r[0],
                        r -> (Long) r[1]
                ));
    }


}

