# P2 Shopping Application – Requirements Checklist

Use this checklist to verify the project against **P2-1_2 Shopping Application Requirement (August 2025)**. Tick or add items from your PDF slides and mark ✅ where the project already conforms.

---

## Authentication & Users

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| User registration | ✅ | `POST /auth/register` with username, email, password |
| User login | ✅ | `POST /auth/login` returns JWT |
| JWT / token-based auth | ✅ | JWT in `Authorization: Bearer <token>` |
| Roles: User vs Admin | ✅ | `ROLE_USER`, `ROLE_ADMIN`; `/user/**`, `/admin/**` |
| Password encoding | ✅ | BCrypt in `SecurityConfig` |

---

## Products

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| Admin: list all products | ✅ | `GET /admin/products` |
| Admin: add product | ✅ | `POST /admin/products` (description, wholesalePrice, retailPrice, quantity) |
| Admin: update product | ✅ | `PUT /admin/products/{id}` |
| Admin: delete product | ✅ | `DELETE /admin/products/{id}` |
| User: list products (e.g. in-stock only) | ✅ | `GET /user/products` (in-stock), `GET /user/products/{id}` |

---

## Orders

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| User: place order | ✅ | `POST /user/orders` with `PlaceOrderRequest` (items: productId, quantity) |
| User: list my orders | ✅ | `GET /user/orders` |
| User: get order by id | ✅ | `GET /user/orders/{id}` |
| User: cancel order | ✅ | `PATCH /user/orders/{id}/cancel` |
| Admin: list all orders | ✅ | `GET /admin/orders` (with user, items, status) |
| Admin: complete order | ✅ | `PATCH /admin/orders/{id}/complete` |
| Admin: cancel order | ✅ | `PATCH /admin/orders/{id}/cancel` |
| Order status (e.g. PENDING, COMPLETED, CANCELLED) | ✅ | `OrderStatus` enum; status transitions in `OrderService` |

---

## Watchlist (if required)

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| User: add product to watchlist | ✅ | `POST /user/watchlist/{productId}` |
| User: remove from watchlist | ✅ | `DELETE /user/watchlist/{productId}` |
| User: get my watchlist | ✅ | `GET /user/watchlist` |

---

## Technical / Stack

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| Spring Boot | ✅ | 2.7.14 |
| REST APIs | ✅ | Controllers under `/auth`, `/user/*`, `/admin/*` |
| Database (e.g. MySQL) | ✅ | Hibernate + MySQL in `application.properties` |
| Validation (e.g. Bean Validation) | ✅ | `@Valid`, `@NotBlank`, `@Email`, `@Positive`, etc. on DTOs |
| Global exception handling | ✅ | `@RestControllerAdvice` in `GlobalExceptionHandler` |
| AOP / logging | ✅ | `LoggingAspect` on service layer (before, after, after-throwing) |
| Inventory check on order | ✅ | `NotEnoughInventoryException` and handling in `OrderService` |

---

## Optional / Extra (check if on slides)

| Requirement (from slides) | Status | Implementation |
|---------------------------|--------|-----------------|
| 201 Created + Location for POST | ⬜ | Currently returns void; add if required |
| Pagination for list endpoints | ⬜ | Not implemented; add if required |
| Product categories | ⬜ | Not in model; add if required |

---

## How to use

1. Open **P2-1_2+Shopping+Application+Requirement(August2025).pdf** and list each requirement from the slides.
2. For each, find the matching row above (or add a new row) and set **Status** to ✅ or ⬜.
3. Where Status is ⬜, either implement the feature or confirm it’s not required by your spec.

If a slide requirement is not listed here, add it under the right section and mark status; then implement or document as needed.
