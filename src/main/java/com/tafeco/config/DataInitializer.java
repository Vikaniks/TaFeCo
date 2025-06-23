package com.tafeco.config;

import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(IRoleUserDAO roleUserDAO) {
        return args -> {
            if (roleUserDAO.findByRole("ROLE_USER").isEmpty()) {
                RoleUser userRole = new RoleUser();
                userRole.setRole("ROLE_USER");
                roleUserDAO.save(userRole);
            }
            if (roleUserDAO.findByRole("ROLE_ADMIN").isEmpty()) {
                RoleUser adminRole = new RoleUser();
                adminRole.setRole("ROLE_ADMIN");
                roleUserDAO.save(adminRole);
            }
            if (roleUserDAO.findByRole("ROLE_MODERATOR").isEmpty()) {
                RoleUser moderatorRole = new RoleUser();
                moderatorRole.setRole("ROLE_MODERATOR");
                roleUserDAO.save(moderatorRole);
            }
            System.out.println("Роли ROLE_USER, ROLE_ADMIN и ROLE_MODERATOR проверены/инициализированы");
        };
    }
}

