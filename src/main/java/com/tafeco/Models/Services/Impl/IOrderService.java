package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.*;
import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOrderService {
    OrderDTO create(OrderDTO dto, String username);

    OrderDTO findById(int id);

    List<OrderDTO> getAll();

    void deleteOrder(int id);

    List<OrderDTO> getByStatus(OrderStatus status);

    List<OrderDTO> getByUserId(Long user);

    List<OrderDTO> getByUserIdAndStatuses(Long user, List<OrderStatus> statuses);

    OrderDetailDTO getOrderDetailById(int order);

    Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void updateOrderStatus(Integer order, String newStatus);

    //OrderDTO changeOrderStatus(Integer orderId, OrderStatus newStatus);

    OrderSummaryDTO getSummary(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String email,
            Long productId,
            Long warehouseId,
            Long storeId
    );


    Page<OrderDTO> findOrders(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String email,
            Long productId,
            Long warehouseId,
            Long storeId,
            Pageable pageable
    );

    List<OrderDTO> findOrdersWithoutPagination(OrderStatus status, LocalDate startDate, LocalDate endDate, String email, Long productId, Long warehouseId, Long storeId);

    void exportToCSV(List<OrderDTO> orders, Writer writer) throws IOException;

    List<Order> findOrdersForExport(OrderStatus status, LocalDate startDate, LocalDate endDate, String email, Long productId, Long warehouseId, Long storeId);

    BigDecimal calculateRevenue(LocalDate startDate, LocalDate endDate);

    Map<OrderStatus, Long> countGroupedByStatus(LocalDate startDate, LocalDate endDate);

    OrderSumReportDTO getSumReport(LocalDate startDate, LocalDate endDate);

    List<OrderStatusReportDTO> groupByStatus();

    List<OrderPeriodReportDTO> groupByPeriod(LocalDate startDate, LocalDate endDate);

    List<OrderSumRangeReportDTO> groupBySumRange();



}


