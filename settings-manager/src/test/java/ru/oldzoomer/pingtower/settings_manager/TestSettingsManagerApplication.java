package ru.oldzoomer.pingtower.settings_manager;

import org.springframework.boot.SpringApplication;

public class TestSettingsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(SettingsManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
