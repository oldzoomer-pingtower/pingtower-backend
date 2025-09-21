# PingTower API Documentation

## Authentication

All PingTower services use Keycloak for authentication and authorization. To access protected endpoints, you need to obtain a JWT token from Keycloak and include it in the `Authorization` header of your requests.

### Obtaining a Token

To obtain a token, make a POST request to the Keycloak token endpoint:

```
POST http://localhost:8080/realms/pingtower/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&client_id=pingtower-backend&username={username}&password={password}
```

Replace `{username}` and `{password}` with your Keycloak user credentials.

### Using a Token

Include the token in the `Authorization` header of your requests:

```
Authorization: Bearer {token}
```

Replace `{token}` with the access token obtained from Keycloak.

## Services

### Pinger Service
- Base URL: http://localhost:8081
- Protected endpoints: `/api/v1/checks/**` (requires ADMIN role)

### Notificator Service
- Base URL: http://localhost:8082
- Protected endpoints: 
  - `/api/v1/channels/**` (requires ADMIN role)
  - `/api/v1/rules/**` (requires ADMIN role)
  - `/api/v1/notificator/**` (requires ADMIN role)

### Statistics Service
- Base URL: http://localhost:8084
- Protected endpoints: `/api/v1/statistics/**` (requires ADMIN role)

## Roles

- `USER` - Basic user role
- `ADMIN` - Administrator role with full access to all endpoints

## Error Responses

When authentication fails, the API will return a 401 Unauthorized response.
When authorization fails (insufficient roles), the API will return a 403 Forbidden response.