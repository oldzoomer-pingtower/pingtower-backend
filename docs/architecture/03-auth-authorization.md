# Authentication and Authorization Architecture

## Overview

PingTower uses Keycloak as its Identity and Access Management (IAM) solution. All services are secured using OAuth 2.0 and OpenID Connect protocols with JWT tokens for authentication and authorization.

## Architecture Components

### Keycloak Server

Keycloak serves as the central authentication server:
- Manages user identities and credentials
- Handles authentication flows (login, registration, password reset)
- Issues JWT tokens with user roles and permissions
- Provides admin console for user and role management
- Supports social login and LDAP/Active Directory integration

### PingTower Realm

The system uses a dedicated `pingtower` realm with:
- **Clients**:
  - `pingtower-backend` - For backend service-to-service communication
  - `pingtower-frontend` - For web/mobile application authentication
- **Roles**:
  - `USER` - Basic user role
  - `ADMIN` - Administrator role with full access

### Service Security Configuration

Each service implements security through the common module:
- Uses Spring Security with OAuth 2.0 Resource Server
- Validates JWT tokens issued by Keycloak
- Extracts user roles from JWT claims
- Enforces role-based access control on endpoints

## Authentication Flow

1. **User Authentication**:
   - User accesses frontend application or API
   - Application redirects to Keycloak login page
   - User provides credentials
   - Keycloak validates credentials and issues JWT token
   - Token is returned to the application

2. **Service-to-Service Authentication**:
   - Services obtain service account tokens from Keycloak
   - Tokens are used for inter-service REST API calls
   - Each service validates incoming tokens

## Authorization Model

### Roles

- **USER**: Basic role for regular users
  - Limited read access to their own data
  - Cannot modify system configuration
- **ADMIN**: Administrative role with full system access
  - Full read/write access to all endpoints
  - Can manage system configuration
  - Can view and modify all user data

### Role Mapping

Keycloak roles are mapped to Spring Security authorities:
- Keycloak role `USER` becomes `ROLE_USER`
- Keycloak role `ADMIN` becomes `ROLE_ADMIN`

### Endpoint Protection

Services protect endpoints based on roles:
- Public endpoints: No authentication required
- User endpoints: Require `USER` or `ADMIN` role
- Admin endpoints: Require `ADMIN` role

## Token Management

### JWT Token Structure

Tokens contain:
- Standard claims (iss, sub, aud, exp, iat)
- User roles in the `realm_access.roles` claim
- User information (username, email, etc.)

### Token Validation

Services validate tokens by:
1. Verifying the token signature using Keycloak's public key
2. Checking token expiration
3. Validating issuer and audience claims
4. Extracting user roles for authorization

### Token Refresh

- Access tokens have a short lifetime (default: 5 minutes)
- Refresh tokens have a longer lifetime (default: 30 minutes)
- Applications automatically refresh access tokens using refresh tokens

## Security Implementation Details

### Common Security Module

The `common` module provides base security configuration:
- `BaseSecurityConfig` - Standard Spring Security configuration
- `KeycloakJwtAuthenticationConverter` - Custom JWT to Authentication converter

### Service-Specific Configuration

Services can extend the base configuration:
- Add service-specific endpoint protection rules
- Customize authentication entry points
- Add additional security filters

## External Access

For external applications:
1. Set the `DOMAIN` environment variable
2. Keycloak is accessible at `http://your-domain:8080`
3. Use the `pingtower-frontend` client for web/mobile applications
4. Use the `pingtower-backend` client for backend service-to-service communication

## Security Best Practices

1. **Token Storage**: 
   - Frontend applications store tokens in memory (not localStorage/sessionStorage)
   - Backend services never log token contents

2. **Transport Security**:
   - All communication with Keycloak uses HTTPS
   - APIs are protected with TLS in production

3. **Token Expiration**:
   - Short-lived access tokens
   - Automatic token refresh in applications

4. **Role-Based Access Control**:
   - Principle of least privilege
   - Regular review of role assignments