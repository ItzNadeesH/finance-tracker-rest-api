[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/xIbq4TFL)

# Personal Finance Tracker - Secure RESTful API

## Introduction
This project is a Secure RESTful API for a Personal Finance Tracker system, designed to help users manage their financial records, track expenses, set budgets, and analyze spending trends. The API ensures secure authentication, data integrity, and a structured approach to personal finance management.

## Features
### 1. User Roles and Authentication
- **Admin**:
    - Manage user accounts.
    - Oversee transactions and reports.
    - Configure system settings (e.g., categories, limits).
- **Regular User**:
    - Add, edit, and delete personal transactions.
    - Set and manage personal budgets.
    - Generate and view financial reports.
- **Security**:
    - JWT-based authentication and session management.

### 2. Expense and Income Tracking
- CRUD operations for transactions.
- Categorization of expenses (e.g., Food, Transportation, Entertainment).
- Custom tagging system for transactions.
- Recurring transactions support.

### 3. Budget Management
- Set monthly and category-specific budgets.
- Budget notifications and recommendations.

### 4. Financial Reports
- Generate reports on spending trends.
- Visual representation of income vs. expenses.
- Filtering by time period, category, or tags.

### 5. Notifications and Alerts
- Unusual spending pattern detection.
- Bill payment reminders.

### 6. Goals and Savings Tracking
- Set financial goals and track progress.
- Automated savings allocation.

### 7. Multi-Currency Support
- Manage finances in multiple currencies.
- Real-time exchange rate updates.

### 8. Role-Based Dashboard
- **Admin Dashboard**: Overview of users, transactions, and financial summaries.
- **User Dashboard**: Personalized summary of transactions, budgets, and goals.

## Technologies Used
- **Backend**: Spring Boot (Java)
- **Database**: MongoDB
- **Authentication**: JWT
- **Testing**: JUnit, Mockito
- **API Documentation**: Postman

## Installation & Setup
1. **Clone Repository**
   ```sh
   git clone <repository-url>
   cd personal-finance-tracker
   ```
2. **Install Dependencies**
   ```sh
   mvn install
   ```
3. **Set Up Environment Variables**
   Create an `application.properties` file in `src/main/resources/` with the following:
   ```properties
   server.port=8080
   spring.data.mongodb.uri=<your_mongodb_connection_string>
   security.jwt.secret-key=<your_secret_key>
   security.jwt.expiration-time=<your_secret_key_expiration_time>
   exchange.rate.api.key=<your_exchange_rate_api_key>
   ```
4. **Run the Application**
   ```sh
   mvn spring-boot:run
   ```
5. **API Documentation**

## API Endpoints
### Authentication
- `POST /api/v1/auth/signup` - Register a new user.
- `POST /api/v1/auth/login` - User login.

### Transactions
- `GET /api/v1/transactions` - Get all transactions.
- `GET /api/v1/transaction/{id}` - Get transaction by id.
- `GET /api/v1/transaction/filter?type=&category=&tags=` - Filter transactions.
- `POST /api/v1/transactions` - Add a transaction.
- `PUT /api/v1/transactions/{id}` - Update a transaction.
- `DELETE /api/v1/transactions/{id}` - Delete a transaction.

### Recurring Transactions
- `GET /api/v1/recurring-transaction` - Get all transactions.
- `GET /api/v1/recurring-transaction/{id}` - Get transaction by id.
- `POST /api/v1/recurring-transaction` - Add a transaction.
- `PUT /api/v1/recurring-transaction/{id}` - Update a transaction.
- `DELETE /api/v1/recurring-transaction/{id}` - Delete a transaction.

### Budgets
- `GET /api/v1/budgets` - Get user budgets.
- `GET /api/v1/budgets/{id}` - Get budget by id.
- `POST /api/v1/budgets` - Set a budget.
- `PUT /api/v1/budgets/{id}` - Update a budget.
- `DELETE /api/v1/budgets/{id}` - Delete a budget.

### Reports
- `GET /api/v1/reports?startDate=&endDate=&category=&tags=` - Get financial report with filters.

### Notifications
- `GET /api/v1/notification` - Get user notification.
- `GET /api/v1/notification/{id}` - Get notification by id.

### Goal
- `GET /api/v1/Goal` - Get user Goal.
- `GET /api/v1/Goal/{id}` - Get Goal by id.
- `POST /api/v1/Goal` - Set a Goal.
- `PUT /api/v1/Goal/{id}` - Update a Goal.
- `PATCH /api/v1/goal/{id}?amount=` - Update Goal progress.
- `DELETE /api/v1/Goal/{id}` - Delete a Goal.
- 
### Currency
- `POST /api/v1/user?currency=` - Change currency.

### Dashboard
- `GET api/v1/dashboard?startDate=&endDate=` - Get dashboard data.
- 
### User
- `GET /api/v1/user/me` - Get current user details.
- `PUT /api/v1/user` - Update current user.
- `PUT /api/v1/user/{id}` - Update a user.
- `PUT /api/v1/user/{id}/promote` - Update a user.
- `DELETE /api/v1/user` - Delete current user.
- `DELETE /api/v1/user/{id}` - Delete a user.

## Testing
### Unit Testing
Run unit tests:
```sh
mvn test
```
### Security Testing
- Use **OWASP ZAP** or **Burp Suite** for security vulnerability checks.

### Performance Testing
- Use **JMeter** for load testing.

## Error Handling & Logging
- Centralized exception handling with `@ControllerAdvice`.
- Logging critical events using SLF4J and Logback.

## Deployment
1. **Build the application**
   ```sh
   mvn package
   ```
2. **Deploy to Server** (e.g., AWS, Heroku)
   ```sh
   heroku create
   git push heroku main
   ```

## Conclusion
This API provides a secure and efficient solution for personal finance management, implementing best practices in authentication, authorization, and performance optimization.


