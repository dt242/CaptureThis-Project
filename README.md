# CaptureThis - Social Media Platform 📸

CaptureThis is a full-stack, cloud-native ready social media web application built with Java 21 and Spring Boot 3. It allows users to share moments, follow other creators, and engage with content through likes and comments. 

The application follows a modern architecture, delegating the comment section logic to an independent RESTful microservice.

## Features
* **User Authentication & Authorization:** Secure registration and login using Spring Security and BCrypt password encoding. Role-based access control (USER and ADMIN).
* **Social Graph:** Follow/unfollow functionality with personalized feeds displaying posts only from followed users.
* **Content Management:** Create, edit, and delete posts. Support for "Draft" and "Published" post statuses.
* **Engagement:** Asynchronous liking system and real-time notifications for likes, comments, and follows.
* **Profile Customization:** Upload profile pictures and edit personal bio/details.
* **Microservice Integration:** Comments are handled by a separate microservice (`CommentsProject`) using Spring's modern `RestClient`.

## Tech Stack
* **Back-end:** Java 21, Spring Boot 3 (Web, Security, Data JPA)
* **Front-end:** Thymeleaf, HTML5, CSS3, JavaScript
* **Database:** MySQL
* **Testing:** JUnit 5, Mockito (with over 70% coverage)

## Getting Started

### Prerequisites
To run this project locally, you need to have the following installed:
* Java Development Kit (JDK) 21
* Maven
* MySQL Server (running on default port 3306)

### Installation & Setup
1. **Clone the repository:**
   `git clone https://github.com/dt242/CaptureThis-Project.git`
2. **Configure Database Credentials:**
   The application is configured to automatically create the required database (`capture-this-app`) on startup. Simply navigate to `src/main/resources/application.yaml` and verify your local MySQL credentials. It uses environment variables with defaults:

       spring:
         datasource:
           url: jdbc:mysql://localhost:3306/capture-this-app?allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=true&serverTimezone=UTC
           username: ${DB_USERNAME:root}
           password: ${DB_PASSWORD:root}

3. **Run the Comments Microservice:**
   **Important:** The main application depends on the `CommentsProject` microservice. Please ensure it is cloned and running on `localhost:8081` before interacting with comments.
4. **Run the Application:**
   `mvn spring-boot:run`
   The application will be accessible at `http://localhost:8080`.

## Test Accounts
The application includes a database seeder that automatically generates dummy data and test users on startup. You can log in immediately using:

* **Regular User:** Username: `testuser` | Password: `123456`
* **Admin User:** Username: `testadmin` | Password: `123456`
