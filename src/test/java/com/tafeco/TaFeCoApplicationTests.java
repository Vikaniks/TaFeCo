package com.tafeco;

import com.tafeco.Models.Services.EmailNotificationSender;
import com.tafeco.Models.Services.WhatsAppNotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"WHATSAPP_TO=+79999999999",
		"WHATSAPP_FROM=+78888888888",
		"WHATSAPP_SID=test",
		"WHATSAPP_TOKEN=test"
})
class TaFeCoApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JavaMailSender javaMailSender() {
			return mock(JavaMailSender.class);
		}

		@Bean
		public WhatsAppNotificationSender whatsAppNotificationSender() {
			return mock(WhatsAppNotificationSender.class);
		}

		@Bean
		public EmailNotificationSender emailNotificationSender() {
			return mock(EmailNotificationSender.class);
		}

	}

	@Test
	void contextLoads() {
	}
}

