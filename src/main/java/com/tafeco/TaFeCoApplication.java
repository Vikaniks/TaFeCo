package com.tafeco;

import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableAsync
public class TaFeCoApplication {

	public static void main(String[] args) {

		SpringApplication.run(TaFeCoApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner init(IRoleUserDAO roleUserDAO,
								  IUserDAO userDAO,
								  PasswordEncoder passwordEncoder) {
		return args -> {
			// 🔹 Инициализация ролей
			Map<String, RoleUser> savedRoles = new HashMap<>();
			String[] roles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_SUPERADMIN"};

			for (String role : roles) {
				try {
					RoleUser roleUser = roleUserDAO.findByRole(role)
							.orElseGet(() -> {
								RoleUser saved = roleUserDAO.save(new RoleUser(null, role));
								if (saved == null) {
									System.err.println("⚠️ Не удалось сохранить роль: " + role);
								}
								return saved;
							});
					if (roleUser != null) {
						savedRoles.put(role, roleUser);
					}
				} catch (Exception e) {
					System.err.println("❌ Ошибка при создании роли '" + role + "': " + e.getMessage());
				}
			}
			System.out.println("✅ Роли инициализированы");

			// 🔹 Создание супер-админа
			String email = "vikaniks@mail.ru";
			System.out.println("🟡 SuperAdmin init...");

			if (userDAO.findByEmail(email).isEmpty()) {
				RoleUser superAdminRole = savedRoles.get("ROLE_SUPERADMIN");

				// Подстраховка: ещё раз проверим в БД
				if (superAdminRole == null) {
					superAdminRole = roleUserDAO.findByRole("ROLE_SUPERADMIN").orElse(null);
				}

				if (superAdminRole == null) {
					System.err.println("❌ Роль SUPER-ADMIN не найдена. Суперадмин не будет создан.");
					return;
				}

				User superAdmin = new User();
				superAdmin.setEmail(email);
				superAdmin.setPassword(passwordEncoder.encode("Westwest21"));
				superAdmin.setName("Victoria");
				superAdmin.setSurname("Admin");
				superAdmin.setPhone("+79312606980");
				superAdmin.setRoles(Set.of(superAdminRole));
				superAdmin.setActive(true);

				userDAO.save(superAdmin);
				System.out.println("✅ Суперадмин успешно создан.");
			} else {
				System.out.println("ℹ️ Суперадмин уже существует.");
			}
		};
	}
}