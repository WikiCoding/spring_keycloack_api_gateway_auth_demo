# Architecture

```text
React + Tailwind → Spring Cloud Gateway → Products instances → PostgreSQL
                         ↘ Keycloak           ↘ Eureka registration
                         ↘ Redis rate limits
                         ↘ Eureka discovery
```

The UI only calls the gateway. The gateway exchanges login credentials with Keycloak and validates JWTs for product requests. Products uses reactive Spring WebFlux/R2DBC; Flyway owns schema migrations.

The product route rewrites `/api/v1/products` to `/products`, applies a Redis-backed limit per JWT subject, and uses Spring Cloud LoadBalancer to choose an instance from Eureka. Responses pass through directly, including downstream error statuses and bodies.

Docker Compose runs PostgreSQL, Keycloak, Redis, and Eureka. The UI, gateway, and products run on the host. Products advertises localhost and its configured port; additional instances use distinct ports. Eureka provides discovery; the gateway performs load balancing. Redis coordinates rate limits across gateways. Eureka and Redis are single-node development infrastructure.

## Technologies

- React, React Router, Axios, Tailwind CSS
- Java 21, Spring Boot WebFlux, Spring Security, OAuth2 Resource Server
- Spring Cloud Gateway Server WebFlux, Eureka, Spring Cloud LoadBalancer, reactive Redis
- Keycloak, PostgreSQL, R2DBC, Flyway, Docker Compose
