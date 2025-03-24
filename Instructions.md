```markdown
# 🍿 Popcorn Palace - Setup Instructions

Welcome to the Popcorn Palace Movie Ticket Booking System! This document provides detailed instructions on how to set up, build, run, and test the project.

## Prerequisites

Before getting started, ensure you have the following tools installed:

- **Java 21 SDK**  
  Download: [Oracle Java 21](https://www.oracle.com/java/technologies/downloads/#java21)

- **Maven**  
  Install via Homebrew (macOS):
  ```bash
  brew install maven
  ```
  Or download from: [Apache Maven](https://maven.apache.org/download.cgi)

- **Docker Desktop**  
  Download: [Docker Desktop](https://www.docker.com/products/docker-desktop/)

- **IDE (Recommended)**  
  IntelliJ IDEA, Eclipse, or VS Code

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/noabenborhoum/popcorn-palace.git
cd popcorn-palace
```

### 2. Start PostgreSQL Database

The project uses PostgreSQL as its database. We've provided a Docker Compose file for easy setup:

```bash
docker compose up -d
```

This command will start a PostgreSQL container with the following configuration:
- Database name: `popcorn-palace`
- Username: `popcorn-palace`
- Password: `popcorn-palace`
- Port: `5432`

### 3. Build the Project

```bash
mvn clean install
```

If you want to skip tests during the build:

```bash
mvn clean install -DskipTests
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will be available at:
- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Movie Management

| Method | Endpoint                  | Description               |
|--------|---------------------------|---------------------------|
| GET    | `/movies/all`             | Get all movies            |
| POST   | `/movies`                 | Add a new movie           |
| POST   | `/movies/update/{title}`  | Update a movie            |
| DELETE | `/movies/{title}`         | Delete a movie            |

### Showtime Management

| Method | Endpoint                    | Description              |
|--------|----------------------------|--------------------------|
| GET    | `/showtimes/{id}`          | Get showtime by ID       |
| POST   | `/showtimes`               | Add a new showtime       |
| POST   | `/showtimes/update/{id}`   | Update a showtime        |
| DELETE | `/showtimes/{id}`          | Delete a showtime        |

### Booking Management

| Method | Endpoint                  | Description               |
|--------|---------------------------|---------------------------|
| GET    | `/bookings/{bookingId}`   | Get booking by ID         |
| GET    | `/bookings/user/{userId}` | Get bookings by user      |
| POST   | `/bookings`               | Create a new booking      |
| DELETE | `/bookings/{bookingId}`   | Cancel a booking          |

## Running Tests

To run all tests:

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=BookingControllerTest
```

## Project Structure

```
├── src
│   ├── main
│   │   ├── java/com/att/tdp/popcorn_palace
│   │   │   ├── controller        # REST controllers
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── MovieController.java
│   │   │   │   └── ShowtimeController.java
│   │   │   ├── dto               # Data Transfer Objects
│   │   │   │   ├── BookingDTO.java
│   │   │   │   ├── MovieDTO.java
│   │   │   │   └── ShowtimeDTO.java
│   │   │   ├── exception         # Custom exceptions
│   │   │   │   ├── ApiException.java
│   │   │   │   ├── ConflictException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidBookingException.java
│   │   │   │   ├── InvalidRequestException.java
│   │   │   │   ├── ResourceExistsException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model             # Entity models
│   │   │   │   ├── Booking.java
│   │   │   │   ├── Movie.java
│   │   │   │   └── Showtime.java
│   │   │   ├── repository        # Data repositories
│   │   │   │   ├── BookingRepository.java
│   │   │   │   ├── MovieRepository.java
│   │   │   │   └── ShowtimeRepository.java
│   │   │   └── service           # Business logic
│   │   │       ├── BookingService.java
│   │   │       ├── MovieService.java
│   │   │       └── ShowtimeService.java
│   │   └── resources
│   │       ├── application.yaml  # Application configuration
│   │       ├── data.sql          # Initial data
│   │       └── schema.sql        # Database schema
│   └── test
│       └── java/com/att/tdp/popcorn_palace
│           ├── controller        # Controller tests
│           │   ├── BookingControllerTest.java
│           │   ├── MovieControllerTest.java
│           │   └── ShowtimeControllerTest.java
│           └── service           # Service tests
│               ├── BookingServiceTest.java
│               ├── MovieServiceTest.java
│               └── ShowtimeServiceTest.java
├── compose.yml                   # Docker Compose configuration
├── pom.xml                       # Maven dependencies
├── README.md                     # Project documentation
└── Instructions.md               # Setup and run instructions
```

## Troubleshooting

If you encounter issues:

1. **Database Connection Issues**
   - Verify Docker is running
   - Check if port 5432 is available (not being used by another process)
   - Ensure the correct database credentials are set in `application.yaml`

2. **Build Failures**
   - Confirm you're using Java 21 (`java -version`)
   - Check Maven configuration
   - Review test failure logs for specific errors

3. **Runtime Errors**
   - Check console/application logs for exceptions
   - Ensure all required services are running

## Additional Notes

- The application uses Spring Boot's auto-configuration to set up the database schema
- Swagger UI is configured to allow interactive API testing
- Tests are written using JUnit and Spring Boot Test

Thank you 
```
