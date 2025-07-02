package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.*;
import com.tafeco.DTO.Mappers.OrderMapper;
import com.tafeco.DTO.Mappers.UserMapper;
import com.tafeco.Exception.ResourceNotFoundException;
import com.tafeco.Models.DAO.IAddressDAO;
import com.tafeco.Models.DAO.IOrderDAO;
import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.*;
import com.tafeco.Models.Services.Impl.*;
import com.tafeco.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


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
    private final ILocalityService localityService;
    private final IDistrictService districtService;
    private final IRegionService regionService;
    private final IStreetService streetService;
    private final IAddressDAO addressDAO;
    private AuthenticationManager authenticationManager;
    private JwtService jwtProvider;

    /**
     * Регистрация нового пользователя:
     * - Проверяет уникальность email
     * - Создает нового пользователя с ролью ROLE_USER
     * - Хэширует пароль
     * - Сохраняет в базе
     */
    @Override
    public LoginResponseDTO register(UserRegisterDTO dto) {
        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = userMapper.fromRegisterDTO(dto);

        Address address = user.getAddress();
        if (address != null) {
            address.setRegion(regionService.findOrCreateByName(address.getRegion().getName()));
            address.setDistrict(districtService.findOrCreateByName(address.getDistrict().getName()));
            address.setLocality(localityService.findOrCreateByName(address.getLocality().getName()));
            address.setStreet(streetService.findOrCreateByName(address.getStreet().getName()));

            Address savedAddress = addressDAO.save(address);
            user.setAddress(savedAddress);
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        RoleUser defaultRole = roleUserDAO.findByRole("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));

        user.getRoles().add(defaultRole);

        User saved = userDAO.save(user);

        // Аутентификация
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерация JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(userDetails);

        // Получаем роль (предполагаю, что у пользователя одна роль)
        String role = saved.getRoles().stream()
                .findFirst()
                .map(RoleUser::getRole)
                .orElse("ROLE_USER");

        // Возвращаем DTO с токеном, ролью и данными пользователя
        return new LoginResponseDTO(jwt, role, userMapper.toDTO(saved));
    }

    /**
     * Обновить профиль пользователя по username.
     * Поддерживает обновление email, имени, адреса доставки.
     * При передаче пароля — проверяет текущий пароль и меняет его.
     */
    @Override
    public UserDTO updateUser(String email, UserUpdateDTO dto) {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Обновление простых полей
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getSurname() != null) user.setSurname(dto.getSurname());

        // Работа с адресом
        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
        }

        if (dto.getLocality() != null) {

            Locality locality = localityService.findByName(dto.getLocality())
                    .orElseGet(() -> {
                        Locality newLocality = new Locality();
                        newLocality.setName(dto.getLocality());
                        return localityService.save(newLocality);
                    });
            address.setLocality(locality);
        }

        if (dto.getDistrict() != null) {
            District district = districtService.findByName(dto.getDistrict())
                    .orElseGet(() -> {
                        District newDistrict = new District();
                        newDistrict.setName(dto.getDistrict());
                        return districtService.save(newDistrict);
                    });
            address.setDistrict(district);
        }

        if (dto.getRegion() != null) {
            Region region = regionService.findByName(dto.getRegion())
                    .orElseGet(() -> {
                        Region newRegion = new Region();
                        newRegion.setName(dto.getRegion());
                        return regionService.save(newRegion);
                    });
            address.setRegion(region);
        }

        if (dto.getStreet() != null) {
            Street street = streetService.findByName(dto.getStreet())
                    .orElseGet(() -> {
                        Street newStreet = new Street();
                        newStreet.setName(dto.getStreet());
                        return streetService.save(newStreet);
                    });
            address.setStreet(street);
        }

        if (dto.getHouse() != null) {
            address.setHouse(dto.getHouse());
        }
        if (dto.getApartment() != null) {
            address.setApartment(dto.getApartment());
        }
        if (dto.getAddressExtra() != null) {
            address.setAddressExtra(dto.getAddressExtra());
        }

        user.setAddress(address);

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
    public UserDTO getUserProfile(String email) {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
        return userMapper.toDTO(user);
    }


    // Удалить пользователя по username.
    // Предварительно проверить на зависимости (активные заказы)
    @Override
    public void deleteUser(String email) {
        User user = userDAO.findByEmail(email)
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
    public void updateUserRole(String email, String role) {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        RoleUser roleUser = roleUserDAO.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Роль " + role + " не найдена"));
        user.getRoles().clear();
        user.getRoles().add(roleUser);
        userDAO.save(user);
    }

    // Получить страницу пользователей с фильтрацией по username/email.
    @Override
    public Page<UserDTO> getUsersWithFilters(String name, String email, Pageable pageable) {
        // можно реализовать фильтрацию с помощью Specification
        return userDAO.findWithFilters(name, email, pageable)
                .map(userMapper::toDTO);
    }

    // Найти пользователя по username для Spring Security.
    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
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
