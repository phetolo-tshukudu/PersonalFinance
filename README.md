# PersonalFinance API

A secure backend REST API for managing personal finances. The application allows users to manage accounts, track income and expenses, create monthly budgets, and securely access financial data using JWT authentication.

The project was developed using Java and Spring Boot with a focus on clean architecture, REST API design, database management, authentication, authorization, and maintainable backend development practices.

---

# Features

## Authentication & Security

* User registration and login
* JWT-based authentication
* BCrypt password encryption
* Stateless authentication using Spring Security
* Protected API endpoints
* Role-based authorization
* Admin-only user management endpoints

---

## User Management

* Register new users
* Retrieve user information
* Admin user management
* Prevent duplicate email registration
* DTO-based data transfer to protect sensitive information

---

## Account Management

* Create financial accounts
* Retrieve authenticated user's accounts
* Retrieve individual accounts
* Transfer money between users
* Account ownership validation
* Balance management

---

## Transaction Management

* Create income and expense transactions
* Retrieve individual transactions
* Retrieve all user transactions
* Budget validation before expenses
* Transaction ownership validation

---

## Budget Management

* Create monthly budgets
* Update budget limits
* Delete budgets
* Monitor spending limits

---

## Exception Handling

The API implements centralized exception handling:

* User not found errors
* Account not found errors
* Transaction not found errors
* Budget not found errors
* Invalid transaction errors
* Access denied errors

All errors return structured responses containing:

* Timestamp
* HTTP status
* Error message

---

## Testing

The project includes:

* Unit testing using JUnit
* Mockito-based service testing
* Repository mocking
* Business logic validation

Current tests include:

* AccountService tests

---

# Technology Stack

## Backend

* Java 21
* Spring Boot 3
* Spring MVC
* Spring Data JPA
* Hibernate ORM

## Security

* Spring Security
* JWT Authentication
* BCrypt Password Encoder

## Database

* MySQL

## Testing

* JUnit
* Mockito

## Build Tool

* Maven

## Version Control

* Git
* GitHub

---

# Application Architecture

The application follows a layered architecture:

```
                Client
                  |
                  ↓
          REST Controller Layer
                  |
                  ↓
             Service Layer
                  |
                  ↓
          Repository Layer
                  |
                  ↓
              MySQL Database
```

---

## Controller Layer

Responsible for:

* Receiving HTTP requests
* Validating input
* Extracting authenticated users
* Returning API responses

---

## Service Layer

Responsible for:

* Business logic
* Validation rules
* Transaction processing
* Data manipulation

---

## Repository Layer

Responsible for:

* Database communication
* CRUD operations
* Custom queries using Spring Data JPA

---

## DTO Layer

DTOs are used to:

* Avoid exposing database entities directly
* Control API request/response structure
* Protect sensitive information

---

# Database Design

Main entities:

## User

Stores authentication and user information.

Attributes:

* ID
* Name
* Email
* Password
* Role

---

## Account

Represents user financial accounts.

Attributes:

* ID
* Account type
* Balance
* User relationship

---

## Transaction

Stores financial activities.

Attributes:

* ID
* Amount
* Category
* Transaction type
* Account relationship
* User relationship

---

## Monthly Budget

Stores monthly spending limits.

Attributes:

* ID
* Budget amount
* User relationship

---

# Authentication Flow

The application uses JWT authentication.

```
User
 |
 | Login credentials
 ↓
AuthenticationManager
 |
 ↓
Validate username/password
 |
 ↓
Generate JWT Token
 |
 ↓
Client stores token
 |
 ↓
JWT Authentication Filter
 |
 ↓
Authorize request
 |
 ↓
Access API resources
```

---

# API Documentation

All protected endpoints require:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# Authentication Endpoints

## Register User

```
POST /auth/register
```

Creates a new user account.

Example request:

```json
{
    "name": "John Doe",
    "email": "john@email.com",
    "password": "password"
}
```

---

## Login

```
POST /auth/login
```

Authenticates a user and returns a JWT token.

Example response:

```json
{
    "token": "jwt-token"
}
```

---

# Account Endpoints

Base URL:

```
/account
```

---

## Create Account

```
POST /account
```

Creates an account for the authenticated user.

---

## Get All User Accounts

```
GET /account
```

Returns all accounts belonging to the logged-in user.

---

## Get Account By ID

```
GET /account/{id}
```

Returns a specific account owned by the authenticated user.

Example:

```
GET /account/1
```

---

## Transfer Money

```
POST /account/transfer
```

Transfers money between users.

Example request:

```json
{
    "transferId": 2,
    "amount": 500.00
}
```

Validation:

* Recipient must exist
* Amount must be valid
* Sender must have enough balance

---

# Transaction Endpoints

Base URL:

```
/transaction
```

---

## Create Transaction

```
POST /transaction
```

Creates a new income or expense transaction.

---

## Get Transaction

```
GET /transaction/{id}
```

Retrieves a specific transaction.

---

## Get All Transactions

```
GET /transaction
```

Retrieves all transactions belonging to the authenticated user.

---

# Budget Endpoints

Base URL:

```
/budget
```

---

## Create Budget

```
POST /budget
```

Creates a monthly budget.

---

## Update Budget

```
PUT /budget/update
```

Updates the user's budget amount.

---

## Delete Budget

```
DELETE /budget/{id}
```

Deletes a budget.

---

# Admin Endpoints

Base URL:

```
/admin/users
```

These endpoints require an authenticated user with:

```
ROLE_ADMIN
```

---

## Get All Users

```
GET /admin/users
```

Returns all registered users.

---

## Create User

```
POST /admin/users
```

Allows administrators to create users.

---

## Get User By ID

```
GET /admin/users/{userId}
```

Retrieves a user by ID.

---

# Running The Project

## Requirements

Install:

* Java 21+
* Maven
* MySQL

---

# Database Setup

Create a MySQL database:

```sql
CREATE DATABASE finances;
```

Configure:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finances
spring.datasource.username=root
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
```

---

# Running Application

Clone repository:

```bash
git clone <repository-url>
```

Navigate:

```bash
cd PersonalFinance
```

Run:

```bash
mvn spring-boot:run
```

Application starts at:

```
http://localhost:8080
```

---

# Running Tests

Execute:

```bash
mvn test
```

---

# Git Workflow

The project follows a feature branch workflow:

```
main
 |
develop
 |
feature branches
```

Example:

```
feature/unit-testing
          |
          ↓
       develop
          |
          ↓
        main
```

Branches are merged into `develop` before being promoted to `main`.

---

# Future Improvements

Planned improvements:

* Swagger/OpenAPI documentation
* Docker containerization
* CI/CD pipeline using GitHub Actions
* Integration testing with Testcontainers
* API pagination
* Advanced financial analytics
* Data visualization dashboard
* Cloud deployment
* AI-powered transaction categorization

---

# Author

## Phetolo Tshukudu

Backend Developer

Skills:

* Java
* Spring Boot
* REST APIs
* Spring Security
* JWT Authentication
* SQL
* Database Design
* Git/GitHub

---

# License

This project is developed for educational and portfolio purposes.
