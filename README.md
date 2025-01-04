<h1 style="text-align: center;">Accommodation Booking Service</h1>

<img src="src/main/resources/templates/Accommodation Booking Service logo.png" alt="Project Logo" width="200"/>

## Introduction

Imagine a booking service in your area offering individuals the opportunity to rent homes, apartments, and other accommodations for their chosen duration. Currently, this service faces significant operational challenges as it relies on antiquated, manual processes for managing properties, renters, financial transactions, and booking records. All data is documented on physical paperwork, creating inefficiencies and limiting the ability to check property availability in real-time. Furthermore, the service only accepts cash payments, leaving out the convenience of credit card transactions.

In this project, we aim to revolutionize the housing rental experience by resolving these issues. Our mission is to develop an advanced online management system for housing rentals. This system will not only simplify the tasks of service administrators but also provide renters with a seamless and efficient platform for securing accommodations, transforming the way people experience housing rentals.

## Technologies and Tools Used

- **Spring Boot**: To create a stand-alone, production-grade Spring-based application.
- **Spring Security**: To handle authentication and authorization.
- **Spring Data JPA**: For database operations.
- **Swagger**: For API documentation.
- **Liquibase**: For database schema changes.
- **Docker**: To containerize the application.
- **Stripe API**: For handling payments.
- **Telegram API**: For sending notifications.

## Main Features
### Controllers and Functions:

1. **Authentication Controller**
    - `POST /register`: Allows users to register a new account.
      - Example request body:
      ```json
      {
      "email": "sych@example.com",
      "password": "password123",
      "repeatPassword": "password123",
      "firstName": "Mike",
      "lastName": "Green"
      }
      ```
    - Example response body:
      ```json
        {
          "id": 1,
          "email": "sych@example.com",
          "firstName": "Mike",
          "lastName": "Green"
        }
      ```
    - `POST /login`: Grants JWT tokens to authenticated users.
       - Example request body:
           ```json
           {
             "email": "sych@example.com",
             "password": "password123"
           }
           ```
          - Example response body:
            ```json
            {
              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
            }
            ```

2. **User Controller**
    - `PUT /users/{id}/role`: Enables users to update their roles, providing role-based access.
    - `GET /users/me`: Retrieves the profile information for the currently logged-in user.
    - `PUT/PATCH /users/me`: Allows users to update their profile information.

3. **Accommodation Controller**
    - `POST /accommodations`: Permits the addition of new accommodations.
    - `GET /accommodations`: Provides a list of available accommodations.
    - `GET /accommodations/{id}`: Retrieves detailed information about a specific accommodation.
    - `PUT/PATCH /accommodations/{id}`: Allows updates to accommodation details, including inventory management.
    - `DELETE /accommodations/{id}`: Enables the removal of accommodations.

4. **Booking Controller**
    - `POST /bookings`: Permits the creation of new accommodation bookings.
    - `GET /bookings/?user_id=...&status=...`: Retrieves bookings based on user ID and their status (Available for managers).
    - `GET /bookings/my`: Retrieves user bookings.
    - `GET /bookings/{id}`: Provides information about a specific booking.
    - `PUT/PATCH /bookings/{id}`: Allows users to update their booking details.
    - `DELETE /bookings/{id}`: Enables the cancellation of bookings.

5. **Payment Controller (Stripe)**
    - `GET /payments/?user_id=...`: Retrieves payment information for users.
    - `POST /payments/`: Initiates payment sessions for booking transactions.
    - `GET /payments/success/`: Handles successful payment processing through Stripe redirection.
    - `GET /payments/cancel/`: Manages payment cancellation and returns payment paused messages during Stripe redirection.

6. **Notifications Service (Telegram)**
    - Notifications about new bookings created/canceled, new created/released accommodations, and successful payments.
    - Other services interact with it to send notifications to booking service administrators.
    - Uses Telegram API, Telegram Chats, and Bots.

### Architecture

![Architecture](src/main/resources/templates/architecture.png)

## Setup and Usage
1. **Clone this repository:**
    ```sh
    git clone https://github.com/4Vitalii5/booking-app.git
    ```
2. **Set up Environment Variables**

    - Use a `.env.template` file and add the values for these variables:
    ```plaintext
    DB_URL=your_database_url
    DB_USERNAME=your_database_username
    DB_PASSWORD=your_database_password
    STRIPE_SECRET_KEY=your_stripe_secret_key
    TELEGRAM_BOT_TOKEN=your_telegram_bot_token
    ```

3. ## Docker
### Docker Setup
1. Create a Docker image:
    ```sh
    docker build -t accommodation-booking-app .
    ```
2. Run the Docker container:
    ```sh
    docker-compose up
    ```

4. **Access the Application**

    - The application will be running at `http://localhost:8081`.

## Challenges Faced and Solutions

1. **Real-Time Property Availability**: Implemented a robust database schema and optimized queries to ensure real-time property availability.
2. **Payment Processing**: Integrated Stripe API to handle secure and efficient payment transactions.
3. **Notification System**: Set up a notification system using Telegram API to alert administrators about important events.

## Tests
## Test Results and Coverage

| Metric | Value |
|--------|-------|
| Total Tests | 105   |
| Passed Tests | 105   |
| Failed Tests | 0     |
| Skipped Tests | 0     |

### Coverage Summary

| Coverage Type | Percentage |
|---------------|------------|
| Lines         | 95%        |
| Branches      | 75%        |
| Method        | 96%        |
| Class             | 100%       |

## Postman Collection
For easy testing and interaction with the API, you can use the Postman collection containing all necessary requests.

### Usage
1. Open Postman and import the [Bokking_App.postman_collection.json](src/main/resources/postman/Booking_App.postman_collection.json).
2. Navigate to the imported Accommodation Booking Service collection.
3. Execute the necessary requests using the appropriate methods and parameters.

> **Note:** Before using the requests, ensure that your local server is running and you have access to the database.
## Video Demonstration

- Watch a [Loom Video](https://www.loom.com/share/3f1840b2718641c2874d44f6e77dc983?sid=6fb2c9d2-f3a2-4e90-9930-e3bf8da12e19) to see how the project works.

## Happy Coding!


---
