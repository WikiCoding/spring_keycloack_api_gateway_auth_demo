# Architecture

```text
React + Tailwind → API gateway → Products service → PostgreSQL
                    ↘ Keycloak
```

The UI only calls the gateway. The gateway exchanges login credentials with Keycloak and validates JWTs for product requests. Products uses reactive Spring WebFlux/R2DBC; Flyway owns schema migrations.

## Technologies

- React, React Router, Axios, Tailwind CSS
- Java 21, Spring Boot WebFlux, Spring Security, OAuth2 Resource Server
- Keycloak, PostgreSQL, R2DBC, Flyway, Docker Compose
