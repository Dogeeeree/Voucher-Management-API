# Voucher Management API

Voucher Management API is a backend-only Spring Boot application for managing promotion vouchers, customer records, and voucher usage history. It was built for an intern developer assessment with emphasis on REST API design, database migrations, validation, service-layer business logic, and clean project structure.

## Features

- Manage vouchers with full CRUD operations.
- Search vouchers by code.
- Manage users with email validation and duplicate protection.
- Record voucher usage history.
- Decrease voucher quantity atomically when a voucher is used.
- Reject voucher usage when the voucher is expired, inactive, or out of stock.
- Return consistent JSON envelopes for both success and error responses.
- Run database schema changes through Flyway migrations.
- Cover core business rules with unit tests and controller tests.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| API | Spring Web |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8+ |
| Migration | Flyway |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |

## Project Structure

```text
src
|-- main
|   |-- java/com/vt1/vouchermanagement
|   |   |-- controller      # REST endpoints
|   |   |-- dto             # Request and response objects
|   |   |-- entity          # JPA entities
|   |   |-- exception       # API exceptions and global handler
|   |   |-- repository      # Spring Data repositories
|   |   |-- service         # Business logic
|   |   `-- VoucherManagementApplication.java
|   `-- resources
|       |-- application.properties
|       `-- db/migration    # Flyway SQL migrations
`-- test
    `-- java/com/vt1/vouchermanagement
        |-- controller
        `-- service
```

## Prerequisites

- Java 17 or newer
- Maven 3.6.3 or newer
- MySQL 8 or newer

Verify your local tools:

```powershell
java -version
mvn -version
```

## Database Setup

Create the database:

```sql
CREATE DATABASE voucher_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

The application uses these default local settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/voucher_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

Override them with environment variables when needed:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/voucher_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
```

Flyway runs automatically on startup:

- `V1__create_voucher_management_tables.sql` creates `users`, `vouchers`, and `voucher_usages`.
- `V2__insert_sample_data.sql` inserts sample users and active vouchers.

## Running the Application

Start the API:

```powershell
mvn spring-boot:run
```

Base URL:

```text
http://localhost:8080
```

## Running Tests

```powershell
mvn test
```

Current verification result:

```text
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## API Response Format

Success response:

```json
{
  "success": true,
  "message": "Create voucher successfully",
  "data": {
    "id": 1,
    "code": "SALE10"
  }
}
```

Error response:

```json
{
  "success": false,
  "message": "Voucher expired"
}
```

## API Reference

### Vouchers

#### List vouchers

```http
GET /vouchers?page=0&size=10
```

#### Search vouchers by code

```http
GET /vouchers/search?code=SALE&page=0&size=10
```

#### Create voucher

```http
POST /vouchers
Content-Type: application/json
```

```json
{
  "code": "SALE20",
  "discountPercent": 20,
  "quantity": 50,
  "expiredDate": "2026-12-31",
  "status": "ACTIVE"
}
```

#### Update voucher

```http
PUT /vouchers/1
Content-Type: application/json
```

```json
{
  "code": "SALE20",
  "discountPercent": 25,
  "quantity": 30,
  "expiredDate": "2026-12-31",
  "status": "ACTIVE"
}
```

#### Delete voucher

```http
DELETE /vouchers/1
```

### Users

#### List users

```http
GET /users?page=0&size=10
```

#### Create user

```http
POST /users
Content-Type: application/json
```

```json
{
  "fullName": "Le Van C",
  "email": "c@gmail.com",
  "phone": "0909123456"
}
```

### Voucher Usages

#### Use voucher

```http
POST /voucher-usages
Content-Type: application/json
```

```json
{
  "userId": 1,
  "voucherId": 1
}
```

#### List usage history

```http
GET /voucher-usages?page=0&size=10
```

## Business Rules

### Voucher rules

- `code` is required and unique.
- `discountPercent` must be between `1` and `100`.
- `quantity` must be greater than or equal to `0`.
- `expiredDate` must be after the current date.
- `status` must be `ACTIVE` or `INACTIVE`.
- A voucher with usage history cannot be deleted.

### User rules

- `fullName` is required.
- `email` is required, valid, and unique.
- `phone` is optional.

### Usage rules

- User and voucher must exist.
- Expired vouchers cannot be used.
- Inactive vouchers cannot be used.
- Vouchers with quantity `0` cannot be used.
- Voucher usage is transactional and locks the voucher row before decrementing quantity.

## Status Codes

| Status | Meaning |
| --- | --- |
| `200 OK` | Read, update, search, or use voucher succeeded |
| `201 Created` | User or voucher created |
| `204 No Content` | Voucher deleted |
| `404 Not Found` | User or voucher does not exist |
| `409 Conflict` | Duplicate data, unusable voucher, or blocked delete |
| `422 Unprocessable Entity` | Request validation failed |
| `500 Internal Server Error` | Unexpected server error |

## Sample Data

Flyway inserts these records during the second migration:

| Type | Values |
| --- | --- |
| Users | `Nguyen Van A <a@gmail.com>`, `Tran Thi B <b@gmail.com>` |
| Vouchers | `SALE10`, `SALE50` |

Sample vouchers expire 90 days after the migration runs, so they remain usable in a fresh local setup.

## Development Notes

- Controllers handle HTTP boundaries only.
- DTOs define request validation and response shape.
- Services own business rules and transaction boundaries.
- Repositories keep data access focused and minimal.
- Flyway is the source of truth for database schema changes.
