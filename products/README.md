# Products service

Reactive product persistence service on port `8081`. It is intentionally unauthenticated; the API gateway protects public access.

```powershell
mvn spring-boot:run
```

Flyway migrations are in `src/main/resources/db/migration`. Override database settings with `PRODUCTS_R2DBC_URL` and `PRODUCTS_JDBC_URL`.
