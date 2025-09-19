package ru.oldzoomer.pingtower.pinger;

import org.springframework.boot.SpringApplication;

public class TestPingerApplication {

	public static void main(String[] args) {
		SpringApplication.from(PingerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}