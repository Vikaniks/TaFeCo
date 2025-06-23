package com.tafeco;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaFeCoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // загружает .env
		System.out.println("DB_USER = " + System.getenv("DB_USER"));
		System.out.println("DB_PASSWORD = " + System.getenv("DB_PASSWORD"));
		System.out.println("JWT_SECRET = " + System.getenv("JWT_SECRET"));
		System.out.println("MAIL_HOST: " + System.getenv("MAIL_HOST"));
		System.out.println("MAIL_PORT: " + System.getenv("MAIL_PORT"));
		SpringApplication.run(TaFeCoApplication.class, args);
	}


}
