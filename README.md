# ForgeMind

A Spring Boot-based social platform that allows users to create, share, and vote on content across different categories. Similar to a combination of Reddit and YouTube's voting system, ForgeMind provides a secure, scalable backend for content management and user interaction.

## 🚀 Features

- **User Authentication & Authorization**
  - Email-based user registration with verification codes
  - JWT token-based authentication
  - Secure password handling with Spring Security

- **Content Management**
  - Create and manage posts across different categories
  - Category-based content organization
  - Real-time content retrieval and filtering

- **Voting System** (In Development)
  - YouTube-like upvote/downvote functionality
  - Individual user vote tracking
  - Vote count aggregation and analytics

- **Email Integration**
  - Automated email verification for new users
  - Spring Mail integration for notifications

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot 3.5.3
- **Language**: Java 24
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Email**: Spring Boot Mail Starter
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok for reducing boilerplate code
  - Jackson for JSON processing
  - JPA/Hibernate for database operations

## 📋 Prerequisites

- Java 24 or higher
- Maven 3.6+
- PostgreSQL database
- SMTP server configuration (for email functionality)

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Naveensivam03/forge-mind.git
   cd forge-mind
   ```

2. **Database Setup**
   - Install PostgreSQL
   - Create a database for the application
   - Update database configuration in `src/main/resources/application.properties`

3. **Environment Configuration**
   - Configure database connection settings
   - Set up SMTP server details for email functionality
   - Configure JWT secret key
   - Update email configuration in `src/main/resources/env`

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080` by default.

## 📚 API Endpoints

### Authentication
- `POST /signup` - Register a new user
- `POST /signup/verify` - Verify email with verification code
- `POST /login` - User login
- `GET /signup/verifyToken` - Verify JWT token

### Content Management
- `GET /allusers` - Get all users (admin)
- `GET /signup/all` - Get all posts
- `GET /signup/allposts/{name}` - Get posts by category
- `GET /signup/allcateg` - Get all categories

### Voting (Current Implementation)
- `POST /signup/interact` - Vote on a post (upvote/downvote)

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/forge_mind/forge_mind/
│   │   ├── Config/           # Security, Email, Web configurations
│   │   ├── Controller/       # REST API controllers
│   │   ├── Dto/             # Data Transfer Objects
│   │   ├── Repository/      # Data access layer
│   │   ├── Service/         # Business logic layer
│   │   └── database/        # Entity models
│   └── resources/
│       ├── application.properties
│       └── env             # Environment variables
└── test/                   # Test files
```

## 🔐 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt password hashing
- **Email Verification**: Two-factor authentication via email
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Request data validation and sanitization

## 🎯 Future Enhancements

### Advanced Voting System
The project includes a comprehensive plan for implementing a YouTube-like voting system with:
- Individual user vote tracking
- Vote history and analytics
- Real-time vote count updates
- Advanced voting algorithms
- Performance optimization for high-volume voting

### Planned Features
- Comment system on posts
- User profiles and activity tracking
- Content moderation tools
- Advanced search and filtering
- Real-time notifications
- Mobile API optimization

## 🐳 Docker Support

The project includes a Dockerfile for containerized deployment:

```bash
# Build Docker image
docker build -t forge-mind .

# Run container
docker run -p 8080:8080 forge-mind
```

## 🧪 Testing

Run tests using Maven:

```bash
./mvnw test
```

## 📖 API Documentation

For detailed API documentation, refer to the controller classes in the `Controller/` package. Each endpoint includes proper request/response handling and error management.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Naveensivam03** - *Initial work* - [GitHub Profile](https://github.com/Naveensivam03)

## 🐛 Issues & Support

If you encounter any issues or have questions, please file an issue on the [GitHub Issues](https://github.com/Naveensivam03/forge-mind/issues) page.

## 🔄 Version History

- **0.0.1-SNAPSHOT** - Initial development version
  - Basic user authentication
  - Content management system
  - Simple voting mechanism
  - Email verification system

---

*Built with ❤️ using Spring Boot*
