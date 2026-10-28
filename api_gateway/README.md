# API gateway

Spring Cloud Gateway Server WebFlux on port `8080`. Spring Security validates Keycloak JWTs, and product routes use Eureka discovery and Spring Cloud LoadBalancer.

Start infrastructure from the repository root with `docker compose up -d --build`, then run products and this gateway:

```powershell
mvn spring-boot:run
```

`POST /api/v1/auth/login` is public for the UI; `/api/v1/products` requires `Authorization: Bearer <token>`.

## Routing and discovery

GET and POST `/api/v1/products` route to `/products` through `lb://products`. Query parameters and bodies pass through; products owns pagination defaults (page 0, size 10), validation, and the 201 create response. Downstream status codes and bodies now pass through unchanged, including validation errors. Only this explicit route is exposed; discovery does not automatically expose other services.

Products registers with Eureka at `http://localhost:8761/eureka/`. The gateway fetches its registry without registering itself. Spring Cloud LoadBalancer uses round-robin selection. Run a second products instance from the repository root in another terminal:

```powershell
mvn -f products/pom.xml spring-boot:run '-Dspring-boot.run.arguments=--server.port=8082'
```

Inspect both instances in the [Eureka dashboard](http://localhost:8761). Registration and caching are asynchronous; allow roughly a minute or more before new instances receive traffic. Failed instances are not removed immediately.

`PRODUCTS_SERVICE_URL` overrides the route with a direct HTTP URL if needed. `EUREKA_URL` overrides the registry URL. Java applications run on the host while Eureka and Redis run in Docker. If containerizing products later, set `EUREKA_INSTANCE_HOSTNAME` to an address reachable from the gateway instead of localhost.

## Rate limiting

Redis stores a token bucket per authenticated JWT subject. GET and POST share the same products bucket. Each request costs one token; an exhausted bucket returns HTTP 429 without forwarding. Gateways sharing Redis share these limits.

| Environment variable | Default |
| --- | --- |
| RATE_LIMIT_REPLENISH_RATE | 10 tokens/second |
| RATE_LIMIT_BURST_CAPACITY | 20 tokens |
| REDIS_HOST | localhost |
| REDIS_PORT | 6379 |

Login is a local controller and is not covered by the product route limiter. Spring's Redis limiter fails open on Redis errors, so enforcement stops during a Redis outage. Redis is ephemeral in this development setup; restarting it resets buckets.

## Tests

Run `mvn -f api_gateway/pom.xml test` from the repository root. Tests send real HTTP requests through the load balancer to two local backends. JWT decoding and limiter decisions are mocked, covering security wiring, rewriting, query/body forwarding, load balancing, login, CORS, and 429 responses without external services. Live Eureka and Redis checks are separate.
