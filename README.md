# Commerce Cart API

Commerce Cart API is a Spring Boot REST service for authentication, product catalog management, shopping carts, and order history. It uses JWT bearer tokens for authenticated operations and Spring Data JPA for persistence.

## Technology stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA and Hibernate
- PostgreSQL for the configured application database
- H2 for test/runtime support
- Spring Security with JWT authentication
- JJWT 0.13.0
- Lombok and MapStruct
- Maven Wrapper

## Requirements

- JDK 21 or newer
- PostgreSQL available locally or at a reachable host

Maven does not need to be installed because this repository includes `mvnw` and `mvnw.cmd`.

## Configuration

The application reads its default settings from `src/main/resources/application.properties`:

```properties
spring.application.name=CommerceCartApi
spring.datasource.url=jdbc:postgresql://localhost:5432/commerce_api_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.jpa.hibernate.ddl-auto=update
```

Create the `commerce_api_db` PostgreSQL database and provide the database password through `spring.datasource.password`. The application also requires a Base64-encoded value for `application.security.jwt.secret-key`. Use private environment-specific values rather than committing credentials or signing keys.

For a local override, set the missing values in `src/main/resources/application.properties`:

```properties
spring.datasource.password=<postgres-password>
application.security.jwt.secret-key=<base64-encoded-secret>
```

Hibernate is configured with `ddl-auto=update`, so the schema is updated automatically at startup. No custom server port is configured; Spring Boot therefore uses port `8080`.

## Run the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Build the application without starting it:

```powershell
.\mvnw.cmd clean package
```

## Authentication and authorization

Registration and login are public. New registrations receive the `USER` role; the API does not expose an admin-registration endpoint.

Login returns a JWT. Send it with protected requests using:

```http
Authorization: Bearer <jwt-token>
```

Tokens expire after 12 hours. The authorization rules implemented in `SecurityConfig` are:

| Endpoint | Access |
| --- | --- |
| `POST /api/auth/register` | Public |
| `POST /api/auth/login` | Public |
| `GET /api/products` | Public |
| `POST /api/products` | `ADMIN` authority |
| `DELETE /api/products/{id}` | `ADMIN` authority |
| Cart and order endpoints | Authenticated users |

## API reference

All request bodies use JSON. Validation rules below reflect the request DTOs in the source code.

### Authentication

#### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com",
  "password": "change-me"
}
```

`firstName` and `lastName` must not be blank, `email` must be a valid email address, and `password` must contain at least six characters. A successful request returns `201 Created` with the created user's `id`, `firstName`, `lastName`, `email`, and `role`.

#### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "ada@example.com",
  "password": "change-me"
}
```

Successful authentication returns `200 OK`:

```json
{
  "token": "<jwt-token>"
}
```

### Products

#### List products

```http
GET /api/products
```

Returns `200 OK` with products whose status is not `DELETED`. Results can therefore include `ACTIVE` and `OUT_OF_STOCK` products.

#### Create a product

```http
POST /api/products
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json
```

```json
{
  "name": "Laptop",
  "description": "13-inch laptop",
  "price": 1299.99
}
```

`name` must not be blank and `price` must be positive. A successful request returns `201 Created` with the product `id`, `name`, `description`, `price`, and `status`. New products are created as `ACTIVE`.

#### Soft-delete a product

```http
DELETE /api/products/{id}
Authorization: Bearer <admin-jwt-token>
```

Returns `200 OK` and changes the product status to `DELETED`; the database row is not physically removed.

### Cart

All cart endpoints require authentication and operate on the current user's cart.

#### Get the current cart

```http
GET /api/cart
Authorization: Bearer <jwt-token>
```

Example response:

```json
{
  "cartId": 1,
  "cartItems": [
    {
      "productId": 1,
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1299.99,
      "totalPrice": 1299.99
    }
  ],
  "cartTotalPrice": 1299.99
}
```

#### Add or increase a cart item

```http
POST /api/cart/items
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "productId": 1,
  "quantity": 1
}
```

`quantity` must be positive. The product must be `ACTIVE`, and the requested total quantity must not exceed available stock.

#### Remove a product from the cart

```http
DELETE /api/cart/items/{productId}
Authorization: Bearer <jwt-token>
```

#### Clear the cart

```http
DELETE /api/cart
Authorization: Bearer <jwt-token>
```

Cart mutation endpoints return the updated cart.

### Orders

All order endpoints require authentication.

#### Create an order

```http
POST /api/orders
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "deliveryAddress": "1 Example Street, London"
}
```

`deliveryAddress` must not be blank. The current cart must contain at least one item, and stock is checked again during order creation. Successful requests return `201 Created`, create an order with `PENDING` status, reduce product stock, and clear the cart.

#### List the current user's orders

```http
GET /api/orders
Authorization: Bearer <jwt-token>
```

Returns `200 OK` with orders containing `orderId`, `deliveryAddress`, `status`, `totalAmount`, `orderDate`, and item details.

## Data model

- `User` stores a unique email, hashed password, and `USER` or `ADMIN` role.
- `Product` stores name, description, price, stock quantity, lifecycle status, and audit timestamps.
- `Cart` belongs one-to-one to a user and contains `CartItem` records.
- `CartItem` references a product and stores its quantity.
- `Order` belongs to a user and contains `OrderItem` records, delivery address, status, total amount, and order date.
- `OrderItem` stores the ordered product, quantity, and unit price captured at order time.

Product statuses are `ACTIVE`, `OUT_OF_STOCK`, and `DELETED`. Order statuses are `PENDING`, `SHIPPED`, `DELIVERED`, and `CANCELED`.

## Error responses

`GlobalExceptionHandler` converts validation failures and domain exceptions into `ApiErrorResponse` responses. Depending on the condition, the API returns `400 Bad Request`, `404 Not Found`, `406 Not Acceptable`, `409 Conflict`, or `500 Internal Server Error`.

## Tests

Run the complete test suite:

Windows:

```powershell
.\mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw test
```

The repository includes:

- `CommerceCartApiApplicationTests` for application context loading
- `ProductControllerTest` for MVC/controller behavior
- `ProductServiceTest` for product service behavior
- `ProductRepositoryTest` for JPA repository behavior
- `ProductIntegrationTest` for a random-port product API flow
