package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserRegisterDTO;
import com.tafeco.DTO.DTO.UserUpdateDTO;
import com.tafeco.DTO.Mappers.OrderMapper;
import com.tafeco.DTO.Mappers.UserMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.IOrderDAO;
import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import com.tafeco.Models.Services.Impl.INotificationService;
import com.tafeco.Models.Services.Impl.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserDAO userDAO;
    private final IRoleUserDAO roleUserDAO;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final IOrderDAO orderRepository;
    private final OrderMapper orderMapper;
    private final INotificationService emailService;

    /**
     * Регистрация нового пользователя:
     * - Проверяет уникальность email
     * - Создает нового пользователя с ролью ROLE_USER
     * - Хэширует пароль
     * - Сохраняет в базе
     */
    @Override
    public UserDTO register(UserRegisterDTO dto) {
        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        if (userDAO.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        User user = userMapper.fromRegisterDTO(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true); // активируем при регистрации

        RoleUser defaultRole = roleUserDAO.findByRole("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));

        user.getRoles().add(defaultRole);       // Назначить пользователю базовую роль

        User saved = userDAO.save(user);        // Сохраняем в базу
        return userMapper.toDTO(saved);         // Вернем DTO
    }

    /**
     * Обновить профиль пользователя по username.
     * Поддерживает обновление email, имени, адреса доставки.
     * При передаче пароля — проверяет текущий пароль и меняет его.
     */
    @Override
    public UserDTO updateUser(String username, UserUpdateDTO dto) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Обновление простых полей
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getDeliveryAddress() != null) user.setDeliveryAddress(dto.getDeliveryAddress());

        // Обновление пароля при наличии нового пароля
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
                throw new IllegalArgumentException("Для смены пароля укажите текущий пароль");
            }
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Текущий пароль введён неверно");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        User updated = userDAO.save(user);
        return userMapper.toDTO(updated);       // Вернем обновленный DTO
    }

    /**
     * Получить профиль пользователя по username.
     */
    @Override
    public UserDTO getUserProfile(String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        return userMapper.toDTO(user);
    }


    // Удалить пользователя по username.
    // Предварительно проверить на зависимости (активные заказы)
    @Override
    public void deleteUser(String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        boolean hasActiveOrders = orderRepository.existsByUserAndStatusIn(
                user,
                List.of(OrderStatus.NEW, OrderStatus.PAID) // список "активных" статусов
        );

        if (hasActiveOrders) {
            throw new IllegalStateException(
                    "Нельзя удалить пользователя с активными заказами (NEW или PAID)"
            );
        }

        userDAO.delete(user); // Удаляем если нет активных заказов
    }


    // Получить список всех пользователей.
    @Override
    public List<UserDTO> getAllUsers() {
        return userDAO.findAll().stream().map(userMapper::toDTO).toList();
    }


    // Обновить роль пользователя (для назначения модератора или администратора).
    @Override
    public void updateUserRole(String username, String role) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        RoleUser roleUser = roleUserDAO.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Роль " + role + " не найдена"));
        user.getRoles().clear();
        user.getRoles().add(roleUser);
        userDAO.save(user);
    }

    // Получить страницу пользователей с фильтрацией по username/email.
    @Override
    public Page<UserDTO> getUsersWithFilters(String username, String email, Pageable pageable) {
        // можно реализовать фильтрацию с помощью Specification
        return userDAO.findWithFilters(username, email, pageable)
                .map(userMapper::toDTO);
    }

    // Найти пользователя по username для Spring Security.
    @Override
    public User findByUsername(String username) {
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    // Обновить статус заказа по id.
    @Override
    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с ID " + orderId + " не найден"));

        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
        order.setStatus(status);
        orderRepository.save(order);
    }


    // Получить список заказов с фильтрацией по статусу и дате.
    @Override
    public Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Order> orders;

        if (status != null && startDate != null && endDate != null) {
            orders = orderRepository.findByStatusAndOrderDateBetween(status, startDate, endDate, pageable);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(orderMapper::toDTO);
    }

    @Override
    public void resetPassword(String email) {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email не найден"));

        String temporaryPassword = generateTemporaryPassword(); // можно использовать UUID
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        user.setPassword(encodedPassword);
        userDAO.save(user);

        emailService.sendTemporaryPassword(email,
                "Временный пароль для входа",
                "Ваш временный пароль: " + temporaryPassword + "\nПожалуйста, измените его после входа.");
    }
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // 8 символов
    }


}
