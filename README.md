# Products application

A reactive products application. The React UI signs in through the API gateway, which protects product operations with Keycloak JWTs. Products are stored in PostgreSQL.

## Run locally

```powershell
docker compose up -d
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

See [architecture](docs/architecture.md) and [user stories](docs/user-stories.md).
