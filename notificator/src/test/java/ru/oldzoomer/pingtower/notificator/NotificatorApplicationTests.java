package ru.oldzoomer.pingtower.notificator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
class NotificatorApplicationTests {

	@Test
	void contextLoads() {
	}

}
