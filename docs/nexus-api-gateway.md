# NexusPay API Gateway Service

The API Gateway is the single entry point for all NexusPay API traffic, handling routing, authentication, rate limiting, and logging.

## Service Structure

```
nexus-api-gateway/
├── src/
│   └── main/
│       ├── java/com/nexuspay/gateway/
│       │   ├── GatewayApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   └── RouteConfig.java
│       │   ├── filter/
│       │   │   ├── AuthenticationFilter.java
│       │   │   ├── RateLimitingFilter.java
│       │   │   └── LoggingFilter.java
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── bootstrap.yml
├── Dockerfile
└── pom.xml
```

## Gateway POM (nexus-api-gateway/pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.nexuspay</groupId>
        <artifactId>nexus-pay-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    
    <artifactId>nexus-api-gateway</artifactId>
    <name>NexusPay API Gateway</name>
    <description>API Gateway for NexusPay platform routing and security</description>
    
    <dependencies>
        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        
        <!-- Spring Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <!-- OAuth2 Resource Server -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        
        <!-- Reactive Redis for rate limiting -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        
        <!-- Service Discovery (optional) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
        
        <!-- Actuator for monitoring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Micrometer for metrics -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Main Application (GatewayApplication.java)

```java
package com.nexuspay.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Identity Service Routes
            .route("identity-service", r -> r
                .path("/api/v1/auth/**", "/api/v1/tenants/**", "/api/v1/users/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway", "nexus-api-gateway")
                    .circuitBreaker(config -> config
                        .setName("identity-service-cb")
                        .setFallbackUri("forward:/fallback/identity")))
                .uri("lb://nexus-identity-service"))
            
            // Billing Service Routes
            .route("billing-service", r -> r
                .path("/api/v1/subscriptions/**", "/api/v1/invoices/**", "/api/v1/plans/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway", "nexus-api-gateway")
                    .circuitBreaker(config -> config
                        .setName("billing-service-cb")
                        .setFallbackUri("forward:/fallback/billing")))
                .uri("lb://nexus-billing-service"))
            
            // Metering Service Routes
            .route("metering-service", r -> r
                .path("/api/v1/events/**", "/api/v1/usage/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway", "nexus-api-gateway")
                    .circuitBreaker(config -> config
                        .setName("metering-service-cb")
                        .setFallbackUri("forward:/fallback/metering")))
                .uri("lb://nexus-metering-service"))
            
            // ML Service Routes
            .route("ml-service", r -> r
                .path("/api/v1/analytics/**", "/api/v1/forecasts/**", "/api/v1/anomalies/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway", "nexus-api-gateway")
                    .circuitBreaker(config -> config
                        .setName("ml-service-cb")
                        .setFallbackUri("forward:/fallback/ml")))
                .uri("lb://nexus-ml-service"))
            .build();
    }
}
```

## Security Configuration (SecurityConfig.java)

```java
package com.nexuspay.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                .pathMatchers(HttpMethod.GET, "/actuator/health", "/actuator/prometheus").permitAll()
                
                // Tenant admin endpoints
                .pathMatchers("/api/v1/tenants/**").hasRole("TENANT_ADMIN")
                
                // Billing endpoints - requires authentication
                .pathMatchers("/api/v1/subscriptions/**", "/api/v1/invoices/**").authenticated()
                
                // Metering endpoints - requires service or tenant admin role
                .pathMatchers("/api/v1/events/**").hasAnyRole("SERVICE", "TENANT_ADMIN")
                
                // ML/Analytics endpoints - requires tenant admin
                .pathMatchers("/api/v1/analytics/**", "/api/v1/forecasts/**").hasRole("TENANT_ADMIN")
                
                // Default - require authentication
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .build();
    }
    
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract roles from JWT claims
            // Implementation depends on your JWT structure
            return jwt.getClaimAsStringList("roles").stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        });
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
```

## Application Configuration (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: nexus-api-gateway
  
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      
      global-filters:
        - name: LoggingFilter
        - name: RateLimitingFilter
        - name: AuthenticationFilter
      
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
        - AddRequestHeader=X-Request-Source, nexus-api-gateway
      
      httpclient:
        connect-timeout: 5000
        response-timeout: 30s
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:9000}
          jwk-set-uri: ${JWT_JWK_SET_URI:http://localhost:9000/.well-known/jwks.json}
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  tracing:
    sampling:
      probability: 0.1

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    com.nexuspay.gateway: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{traceId:-},%X{spanId:-}] %logger{36} - %msg%n"

# Rate limiting configuration
rate-limiting:
  default-requests-per-second: 100
  burst-capacity: 200
  
# Circuit breaker configuration
resilience4j:
  circuitbreaker:
    instances:
      identity-service-cb:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
      billing-service-cb:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
      metering-service-cb:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
      ml-service-cb:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
```

## Dockerfile

```dockerfile
FROM openjdk:21-jre-slim

LABEL maintainer="NexusPay Team"
LABEL service="nexus-api-gateway"

WORKDIR /app

COPY target/nexus-api-gateway-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

The API Gateway provides:

1. **Single entry point** for all API requests
2. **Authentication and authorization** using JWT tokens
3. **Rate limiting** to prevent abuse
4. **Circuit breakers** for resilience
5. **Request routing** to appropriate microservices
6. **Cross-cutting concerns** like logging and monitoring
7. **CORS handling** for web frontend integration