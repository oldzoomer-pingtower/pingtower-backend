package ru.oldzoomer.pingtower.statistics;

import org.springframework.boot.SpringApplication;

public class TestStatisticsApplication {

	public static void main(String[] args) {
		SpringApplication.from(StatisticsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
