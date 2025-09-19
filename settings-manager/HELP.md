# Read Me First

## Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin/packaging-oci-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.5.6/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers Cassandra Module Reference Guide](https://java.testcontainers.org/modules/databases/cassandra/)
* [Testcontainers Kafka Modules Reference Guide](https://java.testcontainers.org/modules/kafka/)
* [Testcontainers Postgres Module Reference Guide](https://java.testcontainers.org/modules/databases/postgres/)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.6/reference/using/devtools.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.5.6/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/3.5.6/specification/configuration-metadata/annotation-processor.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)
* [OAuth2 Client](https://docs.spring.io/spring-boot/3.5.6/reference/web/spring-security.html#web.security.oauth2.client)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/3.5.6/reference/web/spring-security.html#web.security.oauth2.server)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.5.6/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.6/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/3.5.6/reference/data/nosql.html#data.nosql.redis)
* [Spring Data for Apache Cassandra](https://docs.spring.io/spring-boot/3.5.6/reference/data/nosql.html#data.nosql.cassandra)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/3.5.6/reference/messaging/kafka.html)
* [Validation](https://docs.spring.io/spring-boot/3.5.6/reference/io/validation.html)
* [Spring Cache Abstraction](https://docs.spring.io/spring-boot/3.5.6/reference/io/caching.html)
* [Testcontainers](https://java.testcontainers.org/)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Spring Data for Apache Cassandra](https://spring.io/guides/gs/accessing-data-cassandra/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Caching Data with Spring](https://spring.io/guides/gs/caching/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* cassandra: [`cassandra:latest`](https://hub.docker.com/_/cassandra)
* postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)
* redis: [`redis:latest`](https://hub.docker.com/_/redis)

Please review the tags of the used images and set them to the same as you're running in production.

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.5.6/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`cassandra:latest`](https://hub.docker.com/_/cassandra)
* [`apache/kafka-native:latest`](https://hub.docker.com/r/apache/kafka-native)
* [`postgres:latest`](https://hub.docker.com/_/postgres)
* [`redis:latest`](https://hub.docker.com/_/redis)

Please review the tags of the used images and set them to the same as you're running in production.
