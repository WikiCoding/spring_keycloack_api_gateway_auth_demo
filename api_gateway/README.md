# API gateway

Gateway on port `8080`. It authenticates product requests with Keycloak JWTs and forwards them to the products service.

```powershell
mvn spring-boot:run
```

`POST /api/v1/auth/login` is public for the UI; `/api/v1/products` requires `Authorization: Bearer <token>`.
