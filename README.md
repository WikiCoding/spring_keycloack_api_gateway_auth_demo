# Products application

A reactive products application. The React UI signs in through the API gateway, which protects product operations with Keycloak JWTs. Products are stored in PostgreSQL.

## Run locally

```powershell
docker compose up -d --build
cd products; mvn spring-boot:run
cd ..\api_gateway; mvn spring-boot:run
cd ..\products-ui; npm start
```

Open `http://localhost:3000`. Development user: `developer` / `developer`.

## HTTP examples

```powershell
$login = Invoke-RestMethod -Method Post http://localhost:8080/api/v1/auth/login -ContentType application/json -Body '{"username":"developer","password":"developer"}'
$headers = @{ Authorization = "Bearer $($login.access_token)" }
Invoke-RestMethod -Method Post http://localhost:8080/api/v1/products -Headers $headers -ContentType application/json -Body '{"name":"iPhone 17","quantity":3}'
Invoke-RestMethod "http://localhost:8080/api/v1/products?page=0&size=10" -Headers $headers
```

Eureka dashboard: [localhost:8761](http://localhost:8761). Product requests use Redis-backed per-user limits (10 requests/second, burst 20).

See [gateway setup and scaling](api_gateway/README.md), [architecture](docs/architecture.md), and [user stories](docs/user-stories.md).
