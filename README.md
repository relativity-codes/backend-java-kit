

This is the backend API, built with Java Spring Boot. It provides core business logic, authentication, user management, mailing, and more for the Walkre.com ecommerce platform.

## Prerequisites

- **Java Development Kit (JDK) 21 or higher**
- **Apache Maven** (included as `mvnw` in this repo)
- **PostgreSQL or CockroachDB** (with SSL root certificate)
- **An IDE** (IntelliJ IDEA, VS Code, or Eclipse recommended)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/elwalkre-backend-java.git
cd elwalkre-backend-java
```

### 2. Configure Environment

- Edit `src/main/resources/application.properties` to set your database credentials and other environment variables.
- Ensure your SSL certificate is at `src/main/resources/cert/root.crt` (or update the path in your config).

### 3. Build the Project

```bash
./mvnw clean install
```

### 4. Run the Application

#### Using Maven

```bash
./mvnw spring-boot:run
```

#### Or run the packaged JAR

```bash
java -jar target/commerce-maven-0.0.1-SNAPSHOT.jar
```

The server will start on port **8080** by default. You can change this in `application.properties`:

```
server.port=8080
```

### 5. Access the API

- Main API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Database Migrations

- Migration scripts are in `src/main/resources/db/migration/`
- To repair migration history (if needed):

```bash
./mvnw flyway:repair -Dflyway.url='jdbc:postgresql://<host>:<port>/<db>?sslmode=verify-full&user=<user>&password=<password>&sslrootcert=src/main/resources/cert/root.crt'
```

- To apply migrations:

```bash
./mvnw flyway:migrate
```

## Project Structure

- `src/main/java/com/elwalkre/commerce_maven/` — Main Java source code
- `src/main/resources/` — Application config, templates, migrations, certificates
- `src/main/resources/templates/index.html` — Home page for backend
- `src/main/resources/db/migration/` — Flyway migration scripts

## Useful Commands

- **Run tests:**  
    ```bash
    ./mvnw test
    ```
- **Build JAR:**  
    ```bash
    ./mvnw clean package
    ```

## Troubleshooting

- **SSL Certificate Error:**  
    Copy your certificate to `~/.postgresql/root.crt` or update the path in `application.properties`.
- **Migration Errors:**  
    Use `flyway:repair` and check the `flyway_schema_history` table in your database.

## Contributing
