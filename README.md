# Online Shopping App

A backend for an online store, built as two Spring Boot services: an **auth service** that registers users and issues JWTs, and a **shop service** that handles products, orders, and watchlists. Users browse in-stock products and place orders; admins manage inventory, fulfill or cancel orders, and see sales analytics.

Built solo as a training project in the BeaconFire Java full-stack program.

## Tech Stack

- **Java 8**, **Spring Boot 2.7**
- **Spring Security** with **JWT** (HS512), BCrypt password hashing, role-based access (`ROLE_USER`, `ROLE_ADMIN`)
- **Hibernate** (session-based DAOs) with **MySQL**
- **Spring AOP** for service-layer logging
- **Bean Validation** and a global `@RestControllerAdvice` exception handler
- **Maven**, **Lombok**

## Architecture

```
            ┌──────────────────┐   1. register / login    ┌──────────────┐
  Client ──►│  auth-service    │ ───────────────────────► │ MySQL        │
            │  :8080           │ ◄── 2. JWT (user id,     │ auth_service │
            └──────────────────┘     username, role)      └──────────────┘
                     │
                     │ 3. Authorization: Bearer <token>
                     ▼
            ┌──────────────────┐                          ┌───────────────┐
            │  shop-service    │ ───────────────────────► │ MySQL         │
            │  :8081           │                          │ shopping_app  │
            └──────────────────┘                          └───────────────┘
```

- Each service owns its own database.
- The auth service signs a JWT containing the user's id, username, and role. The shop service validates that token in its own `JwtFilter`, so it never calls the auth service per request.
- URL prefixes enforce roles: `/admin/**` requires `ROLE_ADMIN`; `/user/**` is open to users and admins.

Database design: [`shoppingAppERD.png`](shop-service/src/main/resources/shoppingAppERD.png)

## Features

**Users**
- Browse in-stock products (users see name, description, and retail price; wholesale cost and stock count stay admin-only)
- Place orders with multiple items. Inventory is checked and deducted in the same transaction.
- Cancel their own orders while still `PROCESSING`. Stock is restored on cancel.
- View order history, most frequently purchased items, and most recently purchased items
- Add and remove products from a personal watchlist

**Admins**
- Create, update, and delete products (name, description, wholesale price, retail price, quantity)
- View all orders; complete or cancel orders that are `PROCESSING`
- Analytics: top products by units sold and by profit (retail minus wholesale), from completed orders

**Order lifecycle**

```
PROCESSING ──► COMPLETED   (admin)
     │
     └──────► CANCELED     (user or admin; inventory restored)
```

## API

All shop endpoints need an `Authorization: Bearer <token>` header from `/auth/login`.

### Auth service (port 8080)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Create an account (username, email, password) |
| POST | `/auth/login` | Log in and receive a JWT |

### Shop service (port 8081): user

| Method | Endpoint | Description |
|---|---|---|
| GET | `/user/products` | List in-stock products |
| GET | `/user/products/{id}` | Product detail |
| GET | `/user/products/frequent/{limit}` | My most frequently purchased products |
| POST | `/user/orders` | Place an order |
| GET | `/user/orders` | My orders |
| GET | `/user/orders/{id}` | One of my orders |
| PATCH | `/user/orders/{id}/cancel` | Cancel my order |
| GET | `/user/orders/recent-items/{limit}` | My most recently purchased items |
| GET | `/user/watchlist` | My watchlist |
| POST | `/user/watchlist/{productId}` | Add to watchlist |
| DELETE | `/user/watchlist/{productId}` | Remove from watchlist |

### Shop service (port 8081): admin

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/products` | All products |
| GET | `/admin/products/{id}` | Product detail |
| POST | `/admin/products` | Add a product |
| PATCH | `/admin/products/{id}` | Update a product |
| DELETE | `/admin/products/{id}` | Delete a product |
| GET | `/admin/products/popular/{limit}` | Top products by units sold |
| GET | `/admin/products/profit/{limit}` | Top products by profit |
| GET | `/admin/orders` | All orders |
| GET | `/admin/orders/{id}` | Order detail |
| PATCH | `/admin/orders/{id}/complete` | Complete an order |
| PATCH | `/admin/orders/{id}/cancel` | Cancel an order |

### Example

```bash
# Log in
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "password123"}'

# Place an order with the returned token
curl -X POST http://localhost:8081/user/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"items": [{"productId": 1, "quantity": 2}]}'
```

## Running Locally

**Prerequisites:** Java 8+, Maven, MySQL 8

1. Create the two databases:
   ```sql
   CREATE DATABASE auth_service;
   CREATE DATABASE shopping_app;
   ```
2. Set environment variables. Credentials and the JWT key are read from the environment, never stored in the code. Both services must use the **same** `JWT_SECRET`.
   ```bash
   export DB_USERNAME=root                       # optional, defaults to root
   export DB_PASSWORD=your_mysql_password
   export JWT_SECRET=$(openssl rand -base64 64)  # base64-encoded signing key
   ```
3. Start both services from the same terminal session, so they share the variables. Tables are created automatically on startup.
   ```bash
   (cd auth-service && ./mvnw spring-boot:run) &   # http://localhost:8080
   (cd shop-service && ./mvnw spring-boot:run)     # http://localhost:8081
   ```
4. Register a user with `POST /auth/register`, then log in to get a token.

## Error Handling

A global exception handler returns consistent JSON errors, for example:
- `NotEnoughInventoryException` when an order asks for more than is in stock
- `ProductNotAvailableException` for missing or out-of-stock products
- `InvalidCredentialsException` for failed logins
- Validation errors for bad request bodies (`@NotBlank`, `@Email`, `@Positive`, and others)

## Author

**Christian Johnson**: [LinkedIn](https://www.linkedin.com/in/christian-johnson1957) · [GitHub](https://github.com/headnose)
