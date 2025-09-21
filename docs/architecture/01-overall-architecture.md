# PingTower System Architecture

## Overview

PingTower is a microservices-based system designed for monitoring system availability and sending notifications. The architecture follows a modular approach with clearly defined service boundaries and responsibilities.

## System Components

The system consists of the following main components:

### Microservices

1. **Pinger Service** - Responsible for performing system availability checks
   - Executes various types of checks (HTTP, TCP, ICMP, etc.)
   - Scheduled check execution
   - Result storage and basic processing

2. **Notificator Service** - Manages notification channels and rules
   - Configures notification channels (email, SMS, webhooks, etc.)
   - Defines notification rules based on check results
   - Sends notifications when rules are triggered

3. **Statistics Service** - Aggregates and provides statistical data
   - Collects and processes check results
   - Calculates uptime/downtime statistics
   - Provides historical data and trends

### Shared Components

4. **Common Module** - Shared library with common functionality
   - Security configuration
   - Utility classes
   - Shared data models

5. **Keycloak** - Identity and Access Management
   - User authentication and authorization
   - Role-based access control
   - Token management

### Infrastructure

6. **Database** - Data persistence layer
   - Each service has its own database for data isolation
   - PostgreSQL is used as the primary database technology

7. **Message Broker** - Asynchronous communication
   - RabbitMQ or Apache Kafka for inter-service communication
   - Event-driven architecture patterns

## Architecture Patterns

The system implements several architectural patterns:

- **Microservices Architecture** - Services are independently deployable and scalable
- **Event-Driven Architecture** - Services communicate through events when appropriate
- **API Gateway Pattern** - Centralized entry point for external clients (future implementation)
- **Database per Service** - Each service owns its data to ensure loose coupling
- **Shared Library** - Common functionality is shared through the common module