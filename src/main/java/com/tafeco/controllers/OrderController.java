package com.tafeco.controllers;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.OrderDetailDTO;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Services.Impl.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public OrderDTO create(@RequestBody OrderDTO dto, Principal principal) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Список товаров не может быть пустым");
        }

        String username = principal != null ? principal.getName() : null;
        return orderService.create(dto, username);
    }

    @GetMapping("/{id}")
    public OrderDTO getById(@PathVariable int id) {
        return orderService.findById(id);
    }

    @GetMapping
    public List<OrderDTO> getAll() {
        return orderService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        orderService.deleteOrder(id);
    }


    @GetMapping("/status/{status}")
    public List<OrderDTO> getByStatus(@PathVariable OrderStatus status) {
        return orderService.getByStatus(status);
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getByUser(@PathVariable Long userId) {
        return orderService.getByUserId(userId);
    }

    @GetMapping("/user/{userId}/statuses")
    public List<OrderDTO> getByUserAndStatuses(
            @PathVariable Long user,
            @RequestParam List<OrderStatus> statuses) {
        return orderService.getByUserIdAndStatuses(user, statuses);
    }

    @GetMapping("/{id}/detail")
    public OrderDetailDTO getOrderDetail(@PathVariable int id) {
        return orderService.getOrderDetailById(id);
    }

}

