package ru.oldzoomer.pingtower.notificator;

import org.springframework.boot.SpringApplication;

public class TestNotificatorApplication {

	public static void main(String[] args) {
		SpringApplication.from(NotificatorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}