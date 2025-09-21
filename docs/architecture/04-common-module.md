# Common Module Architecture

## Overview

The common module is a shared library that provides reusable components across all PingTower services. It eliminates code duplication and ensures consistent implementation of cross-cutting concerns.

## Module Structure

```
common/
├── build.gradle
├── src/
│   └── main/
│       └── java/
│           └── ru/oldzoomer/pingtower/common/
│               └── security/
│                   ├── BaseSecurityConfig.java
│                   └── KeycloakJwtAuthenticationConverter.java
```

## Components

### Security Package

The security package contains components for authentication and authorization:

#### BaseSecurityConfig

This class provides the base Spring Security configuration for all services:

```java
@Configuration
@EnableWebSecurity
public class BaseSecurityConfig {
    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
        // Standard security configuration
    }
}
```

Key features:
- Configures OAuth2 Resource Server support
- Sets up JWT token validation
- Implements stateless session management
- Disables CSRF protection (not needed for REST APIs)

#### KeycloakJwtAuthenticationConverter

This component converts Keycloak JWT tokens to Spring Security Authentication objects:

```java
@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract roles and create Authentication token
    }
}
```

Key features:
- Extracts roles from Keycloak-specific JWT claims
- Prefixes roles with "ROLE_" for Spring Security compatibility
- Creates JwtAuthenticationToken with proper authorities

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

## Extension Points

Services can extend the base security configuration:

1. **Override Security Rules**: Services can add service-specific endpoint protection rules
2. **Customize Authentication**: Services can add additional authentication mechanisms
3. **Add Security Filters**: Services can register additional security filters

Example of extending security configuration:

```java
@Configuration
@EnableWebSecurity
public class ServiceSecurityConfig extends BaseSecurityConfig {
    
    @Bean
    public SecurityFilterChain serviceSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply base configuration
        baseSecurityFilterChain(http);
        
        // Add service-specific rules
        http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/v1/public/**").permitAll()
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
        );
        
        return http.build();
    }
}
```

## Benefits

1. **Consistency**: All services implement security in the same way
2. **Maintainability**: Security updates only need to be made in one place
3. **Reliability**: Proven security configuration reduces vulnerabilities
4. **Developer Productivity**: Less boilerplate code in individual services

## Future Enhancements

Planned additions to the common module:

1. **Exception Handling**: Standardized exception handling and error responses
2. **Logging**: Common logging configuration and utilities
3. **Monitoring**: Health checks and metrics collection utilities
4. **Validation**: Common validation utilities and annotations
5. **DTOs**: Shared data transfer objects for common entities