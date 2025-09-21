# Service Interactions and Data Flow

## Overview

This document describes how services in the PingTower system interact with each other and the flow of data between components.

## Service Communication Patterns

PingTower uses multiple communication patterns between services:

1. **Synchronous REST API Calls** - Direct HTTP requests between services
2. **Asynchronous Message Passing** - Event-based communication through a message broker
3. **Database Access** - Direct database queries for specific use cases

## Data Flow Scenarios

### Check Execution Flow

1. **Pinger Service** executes scheduled checks
2. Results are stored in the **Pinger Service Database**
3. Events about check results are published to the **Message Broker**
4. **Statistics Service** consumes these events and updates its aggregated data
5. **Notificator Service** consumes these events and evaluates notification rules
6. If rules are triggered, **Notificator Service** sends notifications through configured channels

### Notification Configuration Flow

1. **Notificator Service** manages notification channels and rules through its REST API
2. Configuration data is stored in the **Notificator Service Database**
3. Channel configurations and rules are cached in-memory for performance
4. When check events arrive, they are evaluated against the cached rules

### Statistics Retrieval Flow

1. Clients request statistics through the **Statistics Service** REST API
2. **Statistics Service** retrieves data from its database
3. Data is processed and formatted for the response
4. Response is sent back to the client

## Inter-Service Dependencies

### Pinger Service Dependencies
- **Keycloak** - For authentication of management APIs
- **PostgreSQL** - For storing check configurations and results
- **Message Broker** - For publishing check result events

### Notificator Service Dependencies
- **Keycloak** - For authentication of management APIs
- **PostgreSQL** - For storing notification configurations
- **Message Broker** - For consuming check result events
- **External Notification Services** - Email servers, SMS gateways, webhook endpoints

### Statistics Service Dependencies
- **Keycloak** - For authentication of management APIs
- **PostgreSQL** - For storing aggregated statistics
- **Message Broker** - For consuming check result events

## Message Broker Usage

The message broker is used for asynchronous communication between services:

1. **Topic: check.results**
   - Published by: **Pinger Service**
   - Subscribed by: **Statistics Service**, **Notificator Service**
   - Content: Check execution results with status, timestamp, and metadata

2. **Topic: notifications.send**
   - Published by: **Notificator Service**
   - Subscribed by: Notification handler services
   - Content: Notification details including channel, message, and recipient information

## Error Handling and Resilience

### Circuit Breaker Pattern
Services implement circuit breaker patterns when calling external dependencies to prevent cascade failures.

### Retry Mechanisms
For critical operations, services implement retry mechanisms with exponential backoff.

### Dead Letter Queues
The message broker uses dead letter queues for messages that fail to process after multiple attempts.

## Scalability Considerations

### Horizontal Scaling
Each service can be scaled horizontally based on load:
- **Pinger Service** scales based on the number of checks to execute
- **Notificator Service** scales based on notification volume
- **Statistics Service** scales based on query volume

### Database Sharding
For high-volume scenarios, databases can be sharded based on:
- Time-based partitioning for check results
- Entity-based partitioning for configurations