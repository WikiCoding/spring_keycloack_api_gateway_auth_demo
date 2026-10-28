# Product service notes

- Keep the service reactive: return `Mono`/`Flux`; do not block or call `subscribe()` in application code.
- Keep schema changes as forward-only Flyway migrations.
- Product names are trimmed/lowercased and quantities must be at least 1.
