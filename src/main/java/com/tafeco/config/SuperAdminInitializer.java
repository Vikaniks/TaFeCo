package com.tafeco.config;

import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Set;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final IUserDAO userDAO; // ✅ имя интерфейса
    private final IRoleUserDAO roleUserDAO; // ваш DAO для ролей
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "vikaniks@mail.ru";

        if (userDAO.findByEmail(email).isEmpty()) {
            RoleUser role = roleUserDAO.findByRole("ROLE_SUPERADMIN")
                    .orElseThrow(() -> new RuntimeException("Роль SUPER-Aдмина не найдена"));

            User superAdmin = new User();
            superAdmin.setEmail(email);
            superAdmin.setPassword(passwordEncoder.encode("Westwest21"));
            superAdmin.setName("Victoria");
            superAdmin.setSurname("Admin");
            superAdmin.setRoles(Set.of(role));
            superAdmin.setActive(true);

            userDAO.save(superAdmin);
            System.out.println("✅ Суперадмин создан");
        }
    }
}
