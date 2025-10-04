# Copilot Instructions - Gestor de Tarefas

## Architecture Overview

This is a **hybrid Java application** combining Spring Boot backend with Swing GUI frontend. Both components run in the same JVM process, with the GUI communicating to the backend via HTTP REST APIs over localhost:8080.

### Key Components
- **Backend**: Spring Boot 3.4.1 with JPA, Security, MySQL integration
- **Frontend**: Java Swing with custom UI components and REST client utilities
- **Database**: MySQL (port 3306) with auto-schema creation via Hibernate DDL
- **Communication**: HTTP/JSON between Swing GUI and Spring REST controllers

## Critical Execution Modes

The application supports 3 distinct runtime modes via `GestorTarefasApplication.main()`:

1. **Full Mode** (default): `mvn spring-boot:run` - Starts backend, waits 5 seconds, then launches Swing GUI
2. **Backend Only**: `--spring.profiles.active=backend` - API server only at http://localhost:8080/api
3. **GUI Only**: `--gui` flag - Connects to existing backend instance

```bash
# Key startup scripts
./iniciar_app.sh                    # Full mode with process management
./start_backend.sh                  # Backend only mode
./iniciar_gui.sh                    # GUI only mode
```

## Development Patterns

### HTTP Communication Pattern
All GUI-backend communication uses `HttpUtil` class with Java 11 HttpClient:
- Base URL: `http://localhost:8080/api`
- JSON serialization via Jackson with JavaTimeModule for LocalDateTime
- Custom timeout configurations (10s connect, 30s request)
- Authentication via HTTP Basic (development mode)

### Security Architecture
- Spring Security with **development-friendly** configuration
- CSRF disabled, CORS enabled for all origins
- Custom `Sha256PasswordEncoder` for password hashing
- Demo users auto-created: `demo/demo123`, `admin/admin123`
- Database-driven authentication via `CustomUserDetailsService`

### Entity Relationships
```java
User (profiles: ADMIN, GERENTE, FUNCIONARIO)
  ↓ one-to-many
Task (status: PENDENTE → EM_ANDAMENTO → CONCLUIDA/CANCELADA)
  ↓ one-to-many  
TaskComment
```

## Database Configuration

**MySQL Setup** (application.properties):
- URL: `jdbc:mysql://localhost:3306/gestortarefas?createDatabaseIfNotExist=true`
- Default credentials: root/empty password
- DDL: `hibernate.ddl-auto=update` (preserves data)
- Timezone: UTC to avoid date/time issues

### Initialization Data
Check `TestDataInitializer` and `data/` package for demo data setup patterns.

## Build & Package Commands

```bash
# Development
mvn clean compile                    # Compile only
mvn spring-boot:run                  # Full mode development

# Production
mvn clean package                    # Creates target/gestor-tarefas-1.0.1.jar
java -jar target/gestor-tarefas-1.0.1.jar

# Testing
mvn test                            # Unit tests
```

## Project Structure Conventions

```
src/main/java/com/gestortarefas/
├── config/          # Spring configuration (Security, Data initialization)
├── controller/      # REST API endpoints (@RestController)
├── dto/            # Data Transfer Objects for API communication  
├── gui/            # Swing components (dialogs, frames)
├── model/          # JPA entities (@Entity)
├── repository/     # Spring Data repositories
├── service/        # Business logic layer
├── util/           # HTTP client, CSV export, I18n utilities
└── view/           # Main Swing windows and panels
```

## Common Integration Points

### Adding New API Endpoints
1. Create controller in `controller/` package with `@RestController`
2. Add corresponding service method in `service/` package
3. Update `HttpUtil` or create specific client method for GUI consumption
4. Follow pattern: POST for create, PUT for update, GET for read, DELETE for delete

### GUI Development Patterns
- All Swing components should extend appropriate base classes
- Use `SwingUtilities.invokeLater()` for UI updates from background threads
- HTTP calls should be made on background threads, not EDT
- Follow MVC pattern: GUI → HTTP call → Controller → Service → Repository

### Database Schema Changes
- Modify JPA entities in `model/` package
- Hibernate will auto-update schema due to `ddl-auto=update`
- For data migration, add logic to `TestDataInitializer`

## Debugging & Logging

- Spring Boot logs: Console output shows SQL queries (`show-sql=true`)
- GUI debugging: Check `app_debug.log`, `gui.log` files
- Backend API testing: http://localhost:8080/api/ endpoints
- Database access: Direct MySQL connection (localhost:3306/gestortarefas)

## Dependencies Notes
- Java 17+ required (configured in pom.xml)
- JDatePicker for date selection in Swing
- Apache Commons CSV for export functionality
- MySQL Connector J 8.0.33 for database connectivity
- Jackson with JSR310 module for LocalDateTime JSON handling

When modifying this codebase, maintain the hybrid architecture pattern and ensure both GUI and API modes continue working independently.