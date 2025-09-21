# PingTower Backend Layer

This is PingTower backend layer, based on Java and Spring.

## Architecture

The backend consists of several microservices:

- **pinger** - Service for system availability checking
- **notificator** - Service for managing notifications
- **statistics** - Service for statistics and data aggregation
- **common** - Shared library module with common components

## Authentication and Authorization

The services use Keycloak for authentication and authorization. Keycloak is included in the Docker Compose configuration and will be automatically started with the services.

### Keycloak Configuration

- Realm: `pingtower`
- Default admin user: `admin` / `admin`
- Default regular user: `user` / `user`
- Backend Client ID: `pingtower-backend`
- Backend Client secret: `pingtower-secret`
- Frontend Client ID: `pingtower-frontend`
- Frontend Client secret: `pingtower-frontend-secret`

### External Access

To access Keycloak from external applications:

1. Set the `DOMAIN` environment variable in your `.env` file to your public domain
2. Keycloak will be accessible at:
   - `http://your-domain:8080`
3. Use the `pingtower-frontend` client for web/mobile applications
4. Use the `pingtower-backend` client for backend service-to-service communication

For detailed instructions on configuring external access, see [KEYCLOAK_EXTERNAL_ACCESS.md](KEYCLOAK_EXTERNAL_ACCESS.md).

## Quick Start

1. Copy `.env.example` to `.env` and adjust values if needed:
   ```bash
   cp .env.example .env
   ```

2. For external access, set your domain in the `.env` file:
   ```
   DOMAIN=your-public-domain.com
   ```

3. Start all services:
   ```bash
   docker compose up -d
   ```

4. Access the services:
   - pinger: http://localhost:8081
   - notificator: http://localhost:8082
   - statistics: http://localhost:8084
   - Keycloak: http://localhost:8080
   - Keycloak (external): http://your-domain:8080

## Building Services

To build a specific service, use:
```bash
./gradlew :[service-name]:build
```

For example:
```bash
./gradlew :pinger:build
```
