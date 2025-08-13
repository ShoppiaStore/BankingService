# 🏦 Banking Management System

## 📖 Project Overview
A comprehensive **Banking Management System** built with **Spring Boot** using **Microservices Architecture**. The system provides secure banking operations with robust authentication, authorization, and transaction management capabilities.

## 🏗️ System Architecture

### Microservices Overview
The project consists of **two independent services** that communicate via REST APIs:

#### 1. 🔐 User Security Service (Port: 8080)
**Purpose**: Handles all authentication, authorization, and user management operations.

**Key Features:**
- User Registration with email verification
- JWT-based Authentication & Authorization
- OTP (One-Time Password) verification system
- Password management (forgot/change password)
- Token validation and user ID extraction
- Email notification service
- Security filtering with Spring Security

**Main Endpoints:**
- `POST /rest/auth/register` - User registration
- `POST /rest/auth/login` - User login
- `POST /rest/auth/verify` - Account verification via OTP
- `POST /validateToken` - Token validation
- `POST /extractUserId` - Extract user ID from token
- `POST /forgetPassword` - Forgot password
- `PUT /changePassword` - Change password
- `POST /regenerateOtp` - Regenerate OTP

#### 2. 🏦 Bank Core Service (Port: 8081)
**Purpose**: Manages all banking operations and financial transactions.

**Key Features:**
- Account management (Create, Read, Update, Delete)
- Financial transactions (Deposit, Withdrawal)
- Transaction history tracking
- Balance management
- Card number generation
- Multi-account support per user
- Account type management (CHECKING, SAVINGS, BUSINESS, CREDIT, DOLLAR)

**Main Endpoints:**
- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get account details
- `GET /api/accounts/user` - Get all user accounts
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money
- `GET /api/transactions/history/{accountId}` - Transaction history

### 🔄 Service Communication Pattern
```
Client Application (Angular/Frontend)
            ↓
    [User Security Service] ←→ [Bank Core Service]
            ↓                        ↓
      User Database              Banking Database
```

**Communication Flow:**
1. Client authenticates with User Security Service
2. User Security Service issues JWT token
3. Bank Core Service validates tokens via REST calls to User Security Service
4. All banking operations require valid authentication

## 🛠️ Technologies Used

### Backend Technologies
- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: MySQL
- **ORM**: JPA/Hibernate
- **Communication**: RestTemplate for inter-service communication
- **Validation**: Bean Validation (JSR-303)
- **Documentation**: Swagger/OpenAPI 3
- **Email**: Spring Mail with Gmail SMTP

### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- Spring Boot Starter Mail
- Spring Boot Starter Validation
- MySQL Connector
- JWT (io.jsonwebtoken)
- Lombok
- Swagger UI

## 🗄️ Database Schema

### User Security Database Tables
```sql
-- Users table
users (id, email, password, enabled, phone, name)

-- JWT tokens table
jwt (id, token, created_at, expiration_date, token_type, user_id)

-- OTP table
otp (id, otp, expiration_time, user_id)
```

### Banking Database Tables
```sql
-- Accounts table
account (id, card_number, account_name, balance, account_type, user_id)

-- Transactions table
transactions (id, account_id, amount, notes, transaction_date, balance_after, type)
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- IDE (IntelliJ IDEA/Eclipse)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd banking-system
   ```

2. **Setup MySQL Database**
   ```sql
   CREATE DATABASE banking_app;
   ```

3. **Configure Database Connection**
   
   Update `application.yml` in both services:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/banking_app
       username: your_username
       password: your_password
   ```

4. **Configure Email Service**
   
   Update User Security Service `application.yml`:
   ```yaml
   spring:
     mail:
       host: smtp.gmail.com
       port: 587
       username: your_email@gmail.com
       password: your_app_password
   ```

5. **Run the Services**
   
   **Start User Security Service (Port 8080):**
   ```bash
   cd user-security-service
   mvn spring-boot:run
   ```
   
   **Start Bank Core Service (Port 8081):**
   ```bash
   cd bank-core-service
   mvn spring-boot:run
   ```

## 📚 API Documentation

### Authentication Flow
1. **Register**: `POST /rest/auth/register`
   ```json
   {
     "name": "John Doe",
     "email": "john@example.com",
     "password": "password123",
     "phone": 1234567890
   }
   ```

2. **Verify Account**: `POST /rest/auth/verify?email=john@example.com&otp=123456`

3. **Login**: `POST /rest/auth/login`
   ```json
   {
     "email": "john@example.com",
     "password": "password123"
   }
   ```

4. **Use JWT Token**: Include in headers as `Authorization: Bearer <token>`

### Banking Operations
1. **Create Account**: `POST /api/accounts`
   ```json
   {
     "accountName": "My Savings Account",
     "accountType": "SAVINGS",
     "balance": 1000.0
   }
   ```

2. **Deposit Money**: `POST /api/transactions/deposit`
   ```json
   {
     "cardNumber": "4532123456789012",
     "amount": 500.0
   }
   ```

3. **Withdraw Money**: `POST /api/transactions/withdraw`
   ```json
   {
     "cardNumber": "4532123456789012",
     "amount": 200.0
   }
   ```

## 🔒 Security Features

### JWT Authentication
- **Token Expiration**: 24 hours
- **Secret Key**: Configurable in JwtService
- **Claims**: User ID and email embedded in token

### Security Measures
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Configured for Angular frontend
- **Token Validation**: Every banking operation validates token
- **User Authorization**: Users can only access their own accounts
- **OTP Verification**: Email-based account verification
- **Session Management**: Stateless JWT-based sessions

### Protected Endpoints
All banking endpoints require valid JWT token except:
- Registration and login endpoints
- OTP verification endpoints
- Swagger documentation

## 🎯 Key Features

### User Management
- ✅ Secure user registration with email verification
- ✅ JWT-based authentication
- ✅ Password reset functionality
- ✅ OTP verification system
- ✅ Account activation/deactivation

### Account Management
- ✅ Multiple account types support
- ✅ Auto-generated 16-digit card numbers
- ✅ Account CRUD operations
- ✅ Balance tracking
- ✅ User-specific account access

### Transaction System
- ✅ Secure deposit and withdrawal operations
- ✅ Real-time balance updates
- ✅ Transaction history with timestamps
- ✅ Transaction type tracking
- ✅ Insufficient funds validation

### Security & Validation
- ✅ Input validation with custom error messages
- ✅ Cross-service authentication
- ✅ User authorization checks
- ✅ Comprehensive error handling
- ✅ Audit trail for all operations

## 🌐 API Endpoints Summary

### User Security Service (8080)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/rest/auth/register` | Register new user | No |
| POST | `/rest/auth/login` | User login | No |
| POST | `/rest/auth/verify` | Verify account with OTP | No |
| POST | `/validateToken` | Validate JWT token | No |
| POST | `/extractUserId` | Extract user ID from token | No |
| POST | `/forgetPassword` | Forgot password | Yes |
| PUT | `/changePassword` | Change password | Yes |
| POST | `/regenerateOtp` | Regenerate OTP | No |

### Bank Core Service (8081)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/accounts` | Create new account | Yes |
| GET | `/api/accounts/{id}` | Get account by ID | Yes |
| GET | `/api/accounts/user` | Get user's accounts | Yes |
| PUT | `/api/accounts/{id}` | Update account | Yes |
| DELETE | `/api/accounts/{id}` | Delete account | Yes |
| POST | `/api/transactions/deposit` | Deposit money | No* |
| POST | `/api/transactions/withdraw` | Withdraw money | No* |
| GET | `/api/transactions/history/{accountId}` | Transaction history | Yes |

*Note: Deposit/Withdraw operations use card number instead of user authentication for ATM-like functionality.

## 🔧 Configuration

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

### Inter-Service Communication
```java
// Bank Service calling User Service
private static final String USER_SERVICE_URL = "http://localhost:8080/validateToken";
private static final String USER_SERVICE_URL2 = "http://localhost:8080/extractUserId";
```

## 🧪 Testing

### Testing Strategy
- **Unit Tests**: Service layer testing
- **Integration Tests**: API endpoint testing
- **Security Tests**: Authentication and authorization testing
- **Database Tests**: Repository layer testing

### Manual Testing
Use tools like Postman or curl to test the APIs. Import the provided Postman collection for comprehensive testing.

## 📊 Monitoring & Logging

### Application Monitoring
- Console logging for debugging
- Error tracking and handling
- Performance monitoring capabilities

### Security Monitoring
- Failed authentication attempts
- Token validation logs
- Unauthorized access attempts

## 🚀 Deployment

### Production Considerations
1. **Environment Variables**: Use environment-specific configurations
2. **Database Security**: Use connection pooling and proper credentials
3. **HTTPS**: Enable SSL/TLS for production
4. **Monitoring**: Implement application monitoring
5. **Load Balancing**: Consider load balancing for high availability

### Docker Support
```dockerfile
# Example Dockerfile for each service
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- JWT.io for JWT implementation guidance
- MySQL for reliable database management
- Angular team for frontend framework support

---


## 🔄 Future Enhancements

- [ ] Transfer money between accounts
- [ ] Account statements generation
- [ ] Mobile app integration
- [ ] Advanced reporting and analytics
- [ ] Multi-currency support
- [ ] Credit/Debit card management
- [ ] Loan management system
- [ ] Investment portfolio management
