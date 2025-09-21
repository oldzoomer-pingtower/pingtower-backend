package ru.oldzoomer.pingtower.statistics;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
public abstract class TestConfiguration {

    @Container
    @ServiceConnection
    public static final CassandraContainer cassandra = new CassandraContainer("cassandra")
            .withEnv("CASSANDRA_DC", "datacenter1")
            .withEnv("CASSANDRA_ENDPOINT_SNITCH", "GossipingPropertyFileSnitch")
            .waitingFor(Wait.forSuccessfulCommand("cqlsh -e 'describe keyspaces;'"));

    @Container
    @ServiceConnection
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer("redpandadata/redpanda")
            .waitingFor(Wait.forSuccessfulCommand("rpk cluster health"));

    @Container
    @ServiceConnection
    public static final RedisContainer redisContainer = new RedisContainer("redis:alpine")
            .waitingFor(Wait.forSuccessfulCommand("redis-cli ping"));

}
