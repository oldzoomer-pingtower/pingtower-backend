package ru.oldzoomer.pingtower.pinger;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"))
				.withStartupTimeout(Duration.ofMinutes(2))
				.waitingFor(Wait.forListeningPort())
				.withNetworkAliases("pinger-kafka");
	}

	@SuppressWarnings("resource")
	@Bean
	@ServiceConnection(name = "redis")
	GenericContainer<?> redisContainer() {
		return new GenericContainer<>(DockerImageName.parse("redis:latest"))
				.withExposedPorts(6379)
				.withStartupTimeout(Duration.ofMinutes(1))
				.waitingFor(Wait.forListeningPort())
				.withNetworkAliases("pinger-redis");
	}

}