# Architecture Diagrams

This file contains textual representations of architecture diagrams for PingTower. In a full implementation, these would be actual visual diagrams.

## System Overview Diagram

```
                    ┌─────────────────┐
                    │   User/Browser  │
                    └─────────────────┘
                             │
                    ┌─────────────────┐
                    │   Load Balancer │
                    │    (Optional)   │
                    └─────────────────┘
                             │
                    ┌─────────────────┐
                    │   API Gateway   │
                    │    (Future)     │
                    └─────────────────┘
                    /        │         \
                   /         │          \
         ┌────────────┐  ┌────────────┐  ┌────────────┐
         │  Pinger    │  │Notificator │  │ Statistics │
         │  Service   │  │  Service   │  │  Service   │
         └────────────┘  └────────────┘  └────────────┘
               │               │               │
      ┌────────┴────────┐ ┌────┴────────┐ ┌────┴────────┐
      │ Pinger Database │ │Notificator  │ │Statistics   │
      │ (PostgreSQL)    │ │Database     │ │Database     │
      └─────────────────┘ │(PostgreSQL) │ │(PostgreSQL) │
                          └─────────────┘ └─────────────┘
                   \             │              /
                    \            │             /
                     \           │            /
                      \          │           /
                       \         │          /
                        \        │         /
                         \       │        /
                          \      │       /
                           \     │      /
                            \    │     /
                             \   │    /
                              \  │   /
                               \ │  /
                                \| │
                          ┌─────────────┐
                          │ Message     │
                          │ Broker      │
                          │ (RabbitMQ/  │
                          │  Kafka)     │
                          └─────────────┘
                                 │
                       ┌─────────┴─────────┐
                       │                   │
                ┌─────────────┐    ┌─────────────┐
                │ Keycloak    │    │ External    │
                │ Server      │    │ Services    │
                │             │    │ (Email,     │
                │             │    │  SMS, etc.) │
                └─────────────┘    └─────────────┘
```

## Authentication Flow Diagram

```
1. User Access → 2. Redirect to Keycloak → 3. Login
        │                 │                    │
        └─────────────────┴────────────────────┘
        │                 │                    │
        │          4. Validate Credentials    │
        │                 │                    │
        │                 └────────────────────┘
        │                 │
        │         5. Issue JWT Token
        │                 │
        │                 │
        └─────────────────┘
    6. Return Token to Client
        │
        │
7. Access Protected Resources
        │
        ↓
8. Validate JWT Token (Service)
        │
        ↓
9. Authorize Request (Check Roles)
        │
        ↓
10. Return Response
```

## Data Flow Diagram

```
┌─────────────┐    1. Execute Checks    ┌─────────────┐
│  Pinger     │ ───────────────────────→ │  Check      │
│  Service    │                          │  Results DB │
└─────────────┘                          └─────────────┘
       │                                         │
       │ 2. Publish Check Results                │
       ↓                                         │
┌─────────────┐                                │
│ Message     │                                │
│ Broker      │                                │
└─────────────┘                                │
       │                                         │
       ├─────────────────────────────────────────┘
       │ 3. Consume Check Results
       ↓
┌─────────────┐    4. Update Stats     ┌─────────────┐
│ Statistics  │ ───────────────────────→ │  Stats      │
│ Service     │                          │  Database   │
└─────────────┘                          └─────────────┘
       │
       │ 5. Evaluate Notification Rules
       ↓
┌─────────────┐    6. Send Notifications  ┌─────────────┐
│Notificator  │ ──────────────────────────→ │ External    │
│ Service     │                            │ Services    │
└─────────────┘                            └─────────────┘
       │                                         ↑
       │ 7. Manage Notification Config           │
       └─────────────────────────────────────────┘
```

## Service Communication Patterns

### REST API Calls

```
┌─────────────┐    HTTP Request    ┌─────────────┐
│ Service A   │ ──────────────────→ │ Service B   │
│ (Client)    │ ←────────────────── │ (Server)    │
└─────────────┘    HTTP Response    └─────────────┘
```

### Message Broker Communication

```
┌─────────────┐    Publish Event    ┌─────────────┐
│ Publisher   │ ──────────────────→ │ Message     │
│ Service     │                     │ Broker      │
└─────────────┘                     └─────────────┘
                                              │
                                    ┌─────────┴─────────┐
                                    │                   │
                            ┌─────────────┐    ┌─────────────┐
                            │ Subscriber  │    │ Subscriber  │
                            │ Service 1   │    │ Service 2   │
                            └─────────────┘    └─────────────┘
```

These diagrams provide a visual representation of the system architecture and data flows. In an actual implementation, these would be created as visual diagrams using tools like Draw.io, Lucidchart, or similar diagramming tools.