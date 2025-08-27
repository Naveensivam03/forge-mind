# Forge-Mind - Mermaid Architecture Diagrams

## 1. High-Level System Architecture

```mermaid
graph TB
    User[👤 Client] --> API[🌐 REST API<br/>Spring Boot]
    API --> Auth[🔐 Authentication<br/>JWT + Email Verification]
    API --> Content[📝 Content Management<br/>Posts & Categories]
    API --> Voting[🗳️ Voting System<br/>Community Engagement]
    
    Auth --> DB[(🗄️ PostgreSQL<br/>Database)]
    Content --> DB
    Voting --> DB
    
    Auth --> Email[📧 Email Service<br/>SMTP Gmail]
    
    subgraph "Security Layer"
        Security[🛡️ Spring Security<br/>CORS + JWT]
        Encrypt[🔒 BCrypt<br/>Password Hashing]
    end
    
    API --> Security
    Auth --> Encrypt
    
    style User fill:#e1f5fe
    style API fill:#e8f5e8
    style Auth fill:#fff3e0
    style Content fill:#f3e5f5
    style Voting fill:#fce4ec
    style DB fill:#f1f8e9
```

## 2. Detailed Component Architecture

```mermaid
graph TD
    subgraph "Presentation Layer"
        Controller[userController<br/>REST Endpoints]
        DTO[DTOs<br/>• Signup<br/>• LoginDto<br/>• VerifyDto<br/>• PostResponseDto]
    end
    
    subgraph "Business Logic Layer"
        AuthService[AuthService<br/>• signup()<br/>• login()<br/>• checkVerification()]
        UserService[UserService<br/>• allUsers()]
        PostService[PostService<br/>• allPosts()<br/>• categPosts()]
        VoteService[VoteService<br/>• retrieveAlllikedUser()]
        JwtService[JwtService<br/>• generateTokens()<br/>• verifyToken()]
        EmailService[EmailService<br/>• SendVerificationMail()]
    end
    
    subgraph "Data Access Layer"
        UserRepo[UserRepository<br/>JPA Repository]
        PostRepo[PostRepository<br/>findByCategory_name()]
        VoteRepo[UserVoteRepository<br/>findByUserId()]
        CategoryRepo[CategRepository]
    end
    
    subgraph "Database Entities"
        UserEntity[User Entity<br/>• id, username, email<br/>• password, isEnabled<br/>• verificationCode]
        PostEntity[Posts Entity<br/>• id, content<br/>• upvotes, downvotes<br/>• created_at]
        CategoryEntity[Category Entity<br/>• id, name<br/>• posts (OneToMany)]
        VoteEntity[UserVotes Entity<br/>• user, posts<br/>• voteType (ENUM)]
    end
    
    Controller --> DTO
    Controller --> AuthService
    Controller --> UserService
    Controller --> PostService
    Controller --> VoteService
    
    AuthService --> UserRepo
    AuthService --> JwtService
    AuthService --> EmailService
    PostService --> PostRepo
    VoteService --> VoteRepo
    
    UserRepo --> UserEntity
    PostRepo --> PostEntity
    VoteRepo --> VoteEntity
    CategoryRepo --> CategoryEntity
    
    style Controller fill:#e1f5fe
    style AuthService fill:#e8f5e8
    style UserEntity fill:#fff3e0
    style PostEntity fill:#f3e5f5
```

## 3. Database Entity Relationship Diagram

```mermaid
erDiagram
    User {
        Long id PK
        String username
        String email UK
        String password
        boolean isAccount_enabled
        boolean IsExpired
        String verificationCode
    }
    
    Posts {
        Long id PK
        String content
        Integer upvotes
        Integer downvotes
        LocalDateTime created_at
        Long category_id FK
    }
    
    Category {
        Long id PK
        String name UK
    }
    
    UserVotes {
        Long id PK
        Long user_id FK
        Long posts_id FK
        VoteType voteType
    }
    
    User ||--o{ UserVotes : "user votes on posts"
    Posts ||--o{ UserVotes : "posts receive votes"
    Category ||--o{ Posts : "contains posts"
    
    Posts }o--|| Category : "belongs to category"
```

## 4. Authentication Flow Diagram

```mermaid
sequenceDiagram
    participant Client
    participant Controller as userController
    participant Auth as AuthService
    participant Email as EmailService
    participant JWT as JwtService
    participant DB as Database
    participant SMTP as Gmail SMTP
    
    Note over Client,SMTP: User Registration Flow
    
    Client->>Controller: POST /signup {username, email, password}
    Controller->>Auth: signup(username, email, password)
    Auth->>DB: Check if email exists
    DB-->>Auth: User not found (OK to proceed)
    Auth->>Auth: Hash password with BCrypt
    Auth->>Auth: Generate 6-digit verification code
    Auth->>DB: Save user (disabled, with verification code)
    Auth->>Email: sendVerificationEmail(user)
    Email->>SMTP: Send HTML verification email
    SMTP-->>Email: Email sent successfully
    Email-->>Auth: Email sent
    Auth-->>Controller: "Signup successful! Please check email"
    Controller-->>Client: Success message
    
    Note over Client,SMTP: Email Verification Flow
    
    Client->>Controller: POST /signup/verify {email, code}
    Controller->>Auth: checkVerification(email, code)
    Auth->>DB: Find user by email
    DB-->>Auth: Return user with verification code
    Auth->>Auth: Validate verification code
    Auth->>DB: Enable account & clear verification code
    Auth->>JWT: generateTokens(user)
    JWT-->>Auth: Return JWT token
    Auth-->>Controller: "User verified! JWT: {token}"
    Controller-->>Client: Success with JWT token
    
    Note over Client,SMTP: Login Flow
    
    Client->>Controller: POST /login {email, password}
    Controller->>Auth: login(email, password)
    Auth->>DB: Find user by email
    DB-->>Auth: Return user data
    Auth->>Auth: Verify account is enabled
    Auth->>Auth: Check password with BCrypt
    Auth->>JWT: generateTokens(user)
    JWT-->>Auth: Return JWT token
    Auth-->>Controller: "Login successful!"
    Controller-->>Client: Success response
```

## 5. Content Management Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant PostService
    participant VoteService
    participant PostRepo
    participant VoteRepo
    participant DB as PostgreSQL
    
    Note over Client,DB: Content Discovery
    
    Client->>Controller: GET /allposts
    Controller->>PostService: allPosts()
    PostService->>PostRepo: findAll()
    PostRepo->>DB: SELECT * FROM content JOIN categories
    DB-->>PostRepo: Return posts with category data
    PostRepo-->>PostService: List<Posts>
    PostService->>PostService: Convert to PostResponseDto
    PostService-->>Controller: List<PostResponseDto>
    Controller-->>Client: JSON response with posts
    
    Note over Client,DB: Category Filtering
    
    Client->>Controller: GET /posts/category/{name}
    Controller->>PostService: categPosts(categoryName)
    PostService->>PostRepo: findByCategory_name(categoryName)
    PostRepo->>DB: SELECT * FROM content WHERE category.name = ?
    DB-->>PostRepo: Return filtered posts
    PostRepo-->>PostService: Optional<Posts>
    PostService-->>Controller: List of category posts
    Controller-->>Client: Filtered posts JSON
    
    Note over Client,DB: User Vote History
    
    Client->>Controller: GET /user/{id}/votes
    Controller->>VoteService: retrieveAlllikedUser(userId)
    VoteService->>VoteRepo: findByUserId(userId)
    VoteRepo->>DB: SELECT * FROM userVotes WHERE user_id = ?
    DB-->>VoteRepo: Return user's voting history
    VoteRepo-->>VoteService: List<UserVotes>
    VoteService-->>Controller: User's vote history
    Controller-->>Client: Vote history JSON
```

## 6. Security Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        Browser[Web Browser]
        Mobile[Mobile App]
        API_Client[API Client]
    end
    
    subgraph "Security Gateway"
        CORS[CORS Filter<br/>Cross-Origin Requests]
        Auth_Filter[JWT Authentication Filter]
        CSRF[CSRF Protection<br/>Disabled for API]
    end
    
    subgraph "Authentication System"
        JWT_Service[JWT Service<br/>• Token Generation<br/>• Token Validation<br/>• Claims Extraction]
        Password_Service[Password Security<br/>• BCrypt Hashing<br/>• Salt Generation<br/>• Password Verification]
        Email_Verification[Email Verification<br/>• 6-digit Code Generation<br/>• HTML Email Templates<br/>• Code Validation]
    end
    
    subgraph "Authorization Matrix"
        Public[Public Endpoints<br/>• /signup/**<br/>• /login]
        Protected[Protected Endpoints<br/>• /allUsers<br/>• /verifyToken]
        Admin[Admin Endpoints<br/>• User Management<br/>• System Configuration]
    end
    
    subgraph "Data Protection"
        Encryption[Data Encryption<br/>• Password Hashing<br/>• Sensitive Data Masking]
        Validation[Input Validation<br/>• Email Format<br/>• Password Strength<br/>• SQL Injection Prevention]
    end
    
    Browser --> CORS
    Mobile --> CORS
    API_Client --> CORS
    
    CORS --> Auth_Filter
    Auth_Filter --> CSRF
    
    CSRF --> Public
    CSRF --> Protected
    CSRF --> Admin
    
    Protected --> JWT_Service
    Public --> Password_Service
    Public --> Email_Verification
    
    JWT_Service --> Encryption
    Password_Service --> Encryption
    Email_Verification --> Validation
    
    style CORS fill:#ffebee
    style JWT_Service fill:#e8f5e8
    style Password_Service fill:#fff3e0
    style Encryption fill:#f3e5f5
```

## 7. Deployment Architecture

```mermaid
graph TB
    subgraph "Production Environment"
        subgraph "Application Tier"
            LB[Load Balancer<br/>Nginx/HAProxy]
            App1[Spring Boot Instance 1<br/>Port: 8080]
            App2[Spring Boot Instance 2<br/>Port: 8081]
            App3[Spring Boot Instance 3<br/>Port: 8082]
        end
        
        subgraph "Database Tier"
            DB_Master[(PostgreSQL Master<br/>Read/Write Operations)]
            DB_Replica[(PostgreSQL Replica<br/>Read-Only Operations)]
        end
        
        subgraph "External Services"
            Gmail_SMTP[Gmail SMTP<br/>Email Delivery]
            Monitoring[Application Monitoring<br/>Metrics & Logs]
        end
        
        subgraph "Infrastructure"
            Docker[Docker Containers<br/>Application Packaging]
            K8s[Kubernetes<br/>Container Orchestration]
            Storage[Persistent Storage<br/>Database Files]
        end
    end
    
    subgraph "Development Environment"
        Dev_App[Spring Boot Dev<br/>Local Development]
        Dev_DB[(PostgreSQL Local<br/>Development Database)]
        Dev_Email[MailDev<br/>Email Testing]
    end
    
    LB --> App1
    LB --> App2
    LB --> App3
    
    App1 --> DB_Master
    App2 --> DB_Master
    App3 --> DB_Master
    
    App1 --> DB_Replica
    App2 --> DB_Replica
    App3 --> DB_Replica
    
    App1 --> Gmail_SMTP
    App2 --> Gmail_SMTP
    App3 --> Gmail_SMTP
    
    App1 --> Monitoring
    App2 --> Monitoring
    App3 --> Monitoring
    
    K8s --> Docker
    Docker --> Storage
    
    Dev_App --> Dev_DB
    Dev_App --> Dev_Email
    
    style LB fill:#e1f5fe
    style DB_Master fill:#e8f5e8
    style Gmail_SMTP fill:#fff3e0
    style K8s fill:#f3e5f5
```

## 8. Technology Stack Overview

```mermaid
graph TB
    subgraph "Frontend Tier"
        React[React/Angular<br/>Web Application]
        Mobile[React Native<br/>Mobile Application]
        API_Docs[OpenAPI/Swagger<br/>API Documentation]
    end
    
    subgraph "Backend Tier"
        subgraph "Spring Boot 3.5.3"
            Web[Spring Web<br/>REST Controllers]
            Security[Spring Security<br/>JWT Authentication]
            Data[Spring Data JPA<br/>Repository Pattern]
            Mail[Spring Mail<br/>Email Integration]
        end
        
        subgraph "Java Ecosystem"
            Java24[Java 24<br/>Latest LTS]
            Maven[Apache Maven<br/>Build Tool]
            Lombok[Project Lombok<br/>Code Generation]
        end
    end
    
    subgraph "Database Tier"
        PostgreSQL[(PostgreSQL 15<br/>Primary Database)]
        Hikari[HikariCP<br/>Connection Pooling]
        Hibernate[Hibernate ORM<br/>JPA Implementation]
    end
    
    subgraph "External Services"
        Gmail[Gmail SMTP<br/>Email Service]
        JWT[JWT Library<br/>io.jsonwebtoken]
        BCrypt[BCrypt<br/>Password Hashing]
    end
    
    subgraph "DevOps & Monitoring"
        Docker_Icon[Docker<br/>Containerization]
        K8s_Icon[Kubernetes<br/>Orchestration]
        Prometheus[Prometheus<br/>Metrics Collection]
        ELK[ELK Stack<br/>Logging]
    end
    
    React --> Web
    Mobile --> Web
    API_Docs --> Web
    
    Web --> Security
    Web --> Data
    Web --> Mail
    
    Security --> JWT
    Security --> BCrypt
    Data --> Hibernate
    Data --> Hikari
    Mail --> Gmail
    
    Hibernate --> PostgreSQL
    Hikari --> PostgreSQL
    
    Java24 --> Maven
    Maven --> Lombok
    
    Docker_Icon --> K8s_Icon
    K8s_Icon --> Prometheus
    Prometheus --> ELK
    
    style React fill:#61dafb
    style Security fill:#6db33f
    style PostgreSQL fill:#336791
    style Docker_Icon fill:#2496ed
    style JWT fill:#000000
```

## 9. Error Handling & Resilience

```mermaid
graph TD
    Request[Incoming Request] --> Validation{Input Validation}
    Validation -->|Invalid| ErrorResponse[400 Bad Request<br/>Validation Error]
    Validation -->|Valid| AuthCheck{Authentication Check}
    
    AuthCheck -->|No Token| UnauthorizedError[401 Unauthorized<br/>Missing Token]
    AuthCheck -->|Invalid Token| TokenError[401 Unauthorized<br/>Invalid/Expired Token]
    AuthCheck -->|Valid Token| BusinessLogic[Business Logic Processing]
    
    BusinessLogic --> DBOperation{Database Operation}
    DBOperation -->|Connection Error| DBError[503 Service Unavailable<br/>Database Connection Issue]
    DBOperation -->|Constraint Violation| ConflictError[409 Conflict<br/>Data Integrity Error]
    DBOperation -->|Success| EmailOperation{Email Operation}
    
    EmailOperation -->|SMTP Error| EmailError[502 Bad Gateway<br/>Email Service Error]
    EmailOperation -->|Success| SuccessResponse[200 OK<br/>Success Response]
    
    subgraph "Global Exception Handler"
        GlobalHandler[Exception Handler<br/>@ControllerAdvice]
        LoggingService[Logging Service<br/>Error Tracking]
        AlertService[Alert Service<br/>Critical Error Notifications]
    end
    
    ErrorResponse --> GlobalHandler
    UnauthorizedError --> GlobalHandler
    TokenError --> GlobalHandler
    DBError --> GlobalHandler
    ConflictError --> GlobalHandler
    EmailError --> GlobalHandler
    
    GlobalHandler --> LoggingService
    GlobalHandler --> AlertService
    
    style ErrorResponse fill:#ffebee
    style UnauthorizedError fill:#ffebee
    style DBError fill:#ffebee
    style SuccessResponse fill:#e8f5e8
    style GlobalHandler fill:#fff3e0
```

## Usage Instructions

1. **Copy any diagram** you want to use for presentations
2. **Paste into Mermaid Live Editor** (https://mermaid.live)
3. **Export as PNG/SVG** for documentation or slides
4. **Embed in README** using Mermaid syntax in GitHub

## Interview Strategy

### **Start with Overview** (Diagram 1)
- Show the high-level architecture
- Explain the three main business domains

### **Deep Dive Technical** (Diagrams 2, 3)
- Component architecture shows Spring Boot expertise
- Entity relationship demonstrates database design skills

### **Process Flows** (Diagrams 4, 5)
- Authentication flow shows security understanding
- Content management shows business logic implementation

### **Production Readiness** (Diagrams 6, 7, 9)
- Security architecture demonstrates enterprise thinking
- Deployment architecture shows scalability planning
- Error handling shows production-ready mindset

### **Technology Expertise** (Diagram 8)
- Modern tech stack selection
- Integration complexity management
- DevOps and monitoring awareness

These diagrams demonstrate:
- **Enterprise Architecture Skills**: Complex system design
- **Security Expertise**: Authentication and authorization
- **Database Design**: Normalized relational model
- **API Design**: RESTful service architecture
- **Production Thinking**: Scalability and monitoring
- **Modern Technologies**: Latest Spring Boot and Java
