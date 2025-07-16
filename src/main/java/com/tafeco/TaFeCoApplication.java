package com.tafeco;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaFeCoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // загружает .env
		SpringApplication.run(TaFeCoApplication.class, args);
	}


}
