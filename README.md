# Read Me First

The following was discovered as part of building this project:

### Starting the Application

1. **Install PostgreSQL**: The application uses PostgreSQL as its database. Install PostgreSQL on your system and configure it accordingly.
2. **Run the Application**: Install the necessary dependencies and then run the application.
2. **Run the Application**: For consistency, 'spring.sql.init.mode' must run with 'always' option only once

### Controller Endpoints

Below are the endpoints provided by the application's controllers:

- **GET /api/users**: Retrieves all users.
- **POST /api/users**: Creates a new user.
  example DTO:
  {
  "name": "ali",
  "surname": "veli",
  "username": "aveli",
  "password": "12345",
  "confirmPassword": "12345",
  "creditLimit": "50000"
  }
- **GET /api/users/{id}**: Retrieves the user with the specified ID.
- **PUT /api/users/{id}**: Updates the user with the specified ID.
- **DELETE /api/users/{id}**: Deletes the user with the specified ID.

#### Loan Endpoints

- **POST /api/loans**: Creates a new loan.
- **GET /api/loans**: Retrieves all loans.
- **GET /api/loans/{id}**: Retrieves the loan with the specified ID.
- **PUT /api/loans/{id}**: Updates the loan with the specified ID.
- **DELETE /api/loans/{id}**: Deletes the loan with the specified ID.

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.0/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.4.0/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.0/reference/web/spring-security.html)
