# How to Spin Up a Spring Boot Server

To spin up your Spring Boot server while working on a new project, you typically use your IDE's built-in functionalities or command-line tools. Here's a breakdown of the most common methods:

### Prerequisites

1. **Java Development Kit (JDK):** Ensure you have a JDK installed (Java 11 or higher is commonly used for modern Spring Boot).
2. **Apache Maven or Gradle:** Spring Boot projects are typically built with either Maven or Gradle.
3. **An IDE (Integrated Development Environment):**
      * **IntelliJ IDEA (Ultimate or Community Edition):** Highly recommended for Spring Boot.
      * **Spring Tool Suite (STS):** An Eclipse-based IDE specifically tailored for Spring development.
      * **VS Code:** With the Java Extension Pack and Spring Boot Extension Pack.

### Methods to Spin Up Your Spring Boot Server

#### Method 1: Using an IDE (Recommended)

This is the easiest and most common way, especially during development.

**A. IntelliJ IDEA:**

1. **Open Your Project:**

      * Go to `File > Open...` and select your project's root directory (where `pom.xml` or `build.gradle` is located).
      * IntelliJ will usually detect it's a Maven/Gradle project and import it.

2. **Locate the Main Application Class:**

      * Every Spring Boot application has a main class annotated with `@SpringBootApplication`. It typically looks like this:

        ```java
        package com.example.yourapp;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;

        @SpringBootApplication
        public class YourApplicationNameApplication { // This is your main class

            public static void main(String[] args) {
                SpringApplication.run(YourApplicationNameApplication.class, args);
            }
        }
        ```

      * You'll find this in your `src/main/java/your/package/name` directory.

3. **Run the Application:**

      * **From the Gutter:** Look for a small green "play" (or "run") icon next to the `main` method in your `YourApplicationNameApplication.java` file. Click it and select "Run 'YourApplicationNameApplication'".
      * **From the Run/Debug Configuration:**
          * Go to `Run > Edit Configurations...`
          * Click the `+` button, select "Spring Boot".
          * In the "Main class" field, browse and select your `@SpringBootApplication` class.
          * Give it a name (e.g., "My App").
          * Click "Apply" and then "OK".
          * Now you can click the green "Play" icon in the top right toolbar to run your application.
      * **Using Maven/Gradle Panel:**
          * In the right-hand sidebar, open the "Maven" or "Gradle" tool window.
          * Navigate to `Lifecycle` and double-click `spring-boot:run` (for Maven) or `bootRun` (for Gradle) under your project.

**B. Spring Tool Suite (STS) / Eclipse:**

1. **Import Your Project:**

      * `File > Import... > Maven > Existing Maven Projects` (or `Gradle > Existing Gradle Project`).
      * Browse to your project's root directory.

2. **Locate the Main Application Class:** Same as in IntelliJ (the class with `@SpringBootApplication` and `main` method).

3. **Run the Application:**

      * Right-click on your main application class file in the Package Explorer.
      * Select `Run As > Spring Boot App`.

**C. VS Code:**

1. **Install Extensions:** Make sure you have the "Java Extension Pack" and "Spring Boot Extension Pack" installed.
2. **Open Folder:** `File > Open Folder...` and select your project's root.
3. **Run from Explorer:**
      * In the Explorer view, locate your `src/main/java/your/package/name/YourApplicationNameApplication.java` file.
      * You should see a "Run" | "Debug" button above the `main` method. Click "Run".
4. **From Command Palette:** Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac), type `Spring Boot: Run`, and select your application.

#### Method 2: Using the Command Line

This method is useful for build servers, deployment, or when you prefer a terminal-centric workflow.

**A. Maven:**

1. **Navigate to Project Root:** Open your terminal or command prompt and `cd` into your Spring Boot project's root directory (where `pom.xml` is located).

2. **Build (Optional but Good Practice):**

    ```bash
    mvn clean install
    ```

    This compiles your code, runs tests, and packages your application into a JAR file in the `target/` directory.

3. **Run the Application (using Spring Boot Maven Plugin):**

    ```bash
    mvn spring-boot:run
    ```

    This will start the embedded server.

4. **Run the Packaged JAR:**
    If you've already built the JAR (using `mvn clean install`):

    ```bash
    java -jar target/your-application-name-0.0.1-SNAPSHOT.jar
    ```

    (Replace `your-application-name-0.0.1-SNAPSHOT.jar` with the actual name of your JAR file in the `target` folder.)

**B. Gradle:**

1. **Navigate to Project Root:** Open your terminal or command prompt and `cd` into your Spring Boot project's root directory (where `build.gradle` is located).

2. **Build (Optional but Good Practice):**

    ```bash
    ./gradlew clean build
    ```

    (On Windows, use `gradlew clean build`)
    This compiles, tests, and packages your application.

3. **Run the Application (using Spring Boot Gradle Plugin):**

    ```bash
    ./gradlew bootRun
    ```

    (On Windows, use `gradlew bootRun`)

4. **Run the Packaged JAR:**
    If you've already built the JAR (using `./gradlew clean build`):

    ```bash
    java -jar build/libs/your-application-name-0.0.1-SNAPSHOT.jar
    ```

    (Replace `your-application-name-0.0.1-SNAPSHOT.jar` with the actual name of your JAR file in the `build/libs` folder.)

### What Happens When You Spin Up the Server

* Spring Boot automatically configures and starts an embedded web server (Tomcat, Jetty, or Undertow by default).
* It scans for components (`@Controller`, `@Service`, `@Repository`, `@Component`, etc.) and sets up the application context.
* Your application will typically listen on port 8080 by default (you can change this in `src/main/resources/application.properties` or `application.yml` using `server.port=XXXX`).

### After Starting

Once the server is running, you'll see logs in your console indicating that the embedded server has started and is listening on a specific port. You can then access your API endpoints (e.g., `http://localhost:8080/your-api-endpoint`).

Choose the method that best fits your workflow and IDE preference\! For new projects and active development, running directly from your IDE is usually the most convenient.

### Migration Profiles

If you need to run migrations (e.g., database migrations using Flyway or Liquibase), you can use a specific profile for that purpose.

### Running Migrations

To run migrations, you can use a specific profile. This is useful for setting up your database schema or applying changes before starting the application.
You can run migrations using the following command:

```bash

./mvnw spring-boot:run -Dspring-boot.run.profiles=migrate
```

```bash

java -jar target/your-app.jar --spring.profiles.active=migrate
```

This command will activate the `migrate` profile, which should be configured in your `application.properties` or `application.yml` to run the necessary migration scripts.
