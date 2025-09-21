# PingTower Common Module

This module contains shared components used across all PingTower services.

## Components

### Security
- `KeycloakJwtAuthenticationConverter` - Converts Keycloak JWT tokens to Spring Security Authentication objects
- `BaseSecurityConfig` - Base security configuration for all services

## Usage

To use the common module in a service, add the following dependency to your `build.gradle`:

```gradle
implementation project(':common')
```

Then you can import the security components:

```java
import ru.oldzoomer.pingtower.common.security.KeycloakJwtAuthenticationConverter;
import ru.oldzoomer.pingtower.common.security.BaseSecurityConfig;
```