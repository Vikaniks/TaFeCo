package com.tafeco;

import com.tafeco.Models.DAO.IRoleUserDAO;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class TaFeCoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // загружает .env
		SpringApplication.run(TaFeCoApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner createSuperAdmin(IUserDAO userDAO,
											  IRoleUserDAO roleUserDAO,
											  PasswordEncoder passwordEncoder) {
		return args -> {
			String email = "vikaniks@mail.ru";

			System.out.println("🟡 SuperAdmin init...");

			if (userDAO.findByEmail(email).isEmpty()) {
				RoleUser role = roleUserDAO.findByRole("ROLE_SUPERADMIN")
						.orElseThrow(() -> new RuntimeException("❌ Роль SUPER-Aдмина не найдена!"));

				User superAdmin = new User();
				superAdmin.setEmail(email);
				superAdmin.setPassword(passwordEncoder.encode("Westwest21"));
				superAdmin.setName("Victoria");
				superAdmin.setSurname("Admin");
				superAdmin.setPhone("+79312606980");
				superAdmin.setRoles(Set.of(role));
				superAdmin.setActive(true);

				userDAO.save(superAdmin);

				System.out.println("✅ Суперадмин успешно создан.");
			} else {
				System.out.println("ℹ️ Суперадмин уже существует.");
			}
		};
	}


}
