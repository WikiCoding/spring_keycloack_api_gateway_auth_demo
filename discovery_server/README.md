# Discovery server

A standalone Spring Cloud Netflix Eureka server on port 8761.

Start through the root Compose file with `docker compose up -d --build discovery`, or run `mvn spring-boot:run` here. Dashboard: [localhost:8761](http://localhost:8761).

Products registers instances; the gateway queries the registry. Eureka was chosen because both clients are Spring Boot applications, registration uses a Spring starter, and its dashboard makes discovery easy to learn.

This is a single-node development registry without authentication or peer replication. Its Docker port is bound to loopback.
