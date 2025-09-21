# PingTower - Keycloak External Access Configuration

This document describes how to configure Keycloak for external access to enable authentication from web and mobile applications outside the Docker network.

## Overview

The current Keycloak setup supports two types of clients:
1. **pingtower-backend** - For backend service-to-service communication
2. **pingtower-frontend** - For external web/mobile applications

## Prerequisites

1. A domain name pointing to your server

## Configuration Steps

### 1. Set Domain Environment Variable

In your `.env` file, set the `DOMAIN` variable to your public domain:

```env
DOMAIN=your-public-domain.com
```

### 2. Start Services

Start all services with Docker Compose:

```bash
docker compose up -d
```

### 3. Access Keycloak

Keycloak will be accessible at:
- HTTP: `http://your-domain:8080`

## External Client Configuration

### Client ID: `pingtower-frontend`

This client is pre-configured for external applications with:
- Public client type (no client secret required for frontend)
- Wildcard redirect URIs for flexibility
- Enabled implicit and authorization code flows
- Direct access grants enabled

### Using the External Client

For web applications, use the following configuration:

```javascript
{
  "realm": "pingtower",
  "url": "http://your-domain:8080",
  "ssl-required": "external",
  "resource": "pingtower-frontend",
  "public-client": true,
  "confidential-port": 0
}
```

For mobile applications, use:

```json
{
  "realm": "pingtower",
  "auth-server-url": "http://your-domain:8080/",
  "ssl-required": "external",
  "resource": "pingtower-frontend",
  "public-client": true
}
```

## Testing External Access

### 1. Check Keycloak Availability

```bash
curl -f http://your-domain:8080/realms/pingtower/.well-known/openid-configuration
```

### 2. Test Authentication Flow

Use the following URL to initiate the authentication flow:
```
http://your-domain:8080/realms/pingtower/protocol/openid-connect/auth?client_id=pingtower-frontend&response_type=code&redirect_uri=http://localhost:3000/auth/callback
```

## Troubleshooting

### Common Issues

1. **Redirect URI not whitelisted**: Ensure your application's redirect URI is included in the client configuration
2. **CORS errors**: Check that web origins are properly configured in the client settings
3. **Connection refused**: Verify that your domain points to the correct server and port 8080 is accessible

### Logs

Check Keycloak logs for errors:
```bash
docker compose logs keycloak
```

## Security Considerations

1. For production environments, consider using a reverse proxy with SSL termination
2. Restrict redirect URIs to specific domains in production
3. Use confidential clients for applications that can securely store client secrets
4. Regularly rotate client secrets
5. Monitor authentication logs for suspicious activity