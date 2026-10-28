# API gateway notes

- Keep product routes JWT-protected; only login and CORS preflight are public.
- Keep this a thin gateway: no product persistence or business rules.
- Forward product calls to `products.service.url`.
