package ru.oldzoomer.pingtower.settings_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SettingsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SettingsManagerApplication.class, args);
	}

}
