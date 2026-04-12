# Spring Boot Demo

This is a basic Spring Boot application with Maven, including Lombok for boilerplate reduction, H2 in-memory database for testing with console access, and PostgreSQL driver for production.

## Running the Application

Run `mvn spring-boot:run` to start the application.

The application will be available at http://localhost:8080

H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)

## Building

`mvn clean compile`

## Testing

`mvn test`