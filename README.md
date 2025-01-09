<h1 style="text-align: center;">Accommodation Booking Service</h1>

<img src="src/main/resources/images/Accommodation Booking Service logo.png" alt="Project Logo" width="200"/>

## Introduction

In this project, we created a booking service for renting apartments or houses in any region. Bookings are available for the period selected by users. Payment for bookings can be conveniently made through the Stripe service using a credit card.
Our mission was to develop an advanced online rental management system for housing. This system will not only simplify the tasks of service administration but also provide tenants with a seamless and efficient platform for securing housing, changing the way housing is rented.

## Technologies and Tools Used

- **Spring Boot v3.3.4**: To create a stand-alone, production-grade Spring-based application.
- **Spring Security v3.3.4**: To handle authentication and authorization.
- **Spring Data JPA v3.3.4**: For database operations.
- **Swagger v2.1.0**: For API documentation.
- **Liquibase v4.27.0**: For database schema changes.
- **Docker v1.18.0**: To containerize the application.
- **Stripe API v28.1.0**: For handling payments.
- **Telegram API v6.7.0**: For sending notifications.

## Main Features 

- **Authentication**: Register and log in to the system. 
- **User Management**: Update user roles and profile information. 
- **Accommodation Management**: Add, update, retrieve, and delete accommodations. 
- **Booking Management**: Create, update, retrieve, and cancel bookings. 
- **Payment Processing**: Handle payment sessions and responses using Stripe. 
- **Notification System**: Send notifications about new bookings, cancellations, and payments using Telegram.

### Architecture

![Architecture](src/main/resources/images/architecture.png)

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

### Docker Setup

1. Create a Docker image:
    ```sh
    docker build -t accommodation-booking-app .
    ```
2. Run the Docker container:
    ```sh
    docker-compose up
    ```

3. **Access the Application**

    - The application will be running at `http://localhost:8081/api`.

## Challenges Faced and Solutions

1. **Real-Time Property Availability**: Implemented a robust database schema and optimized queries to ensure real-time property availability.
2. **Payment Processing**: Integrated Stripe API to handle secure and efficient payment transactions.
3. **Notification System**: Set up a notification system using Telegram API to alert administrators about important events.

## Tests

## Test Results and Coverage

| Metric        | Value |
|---------------|-------|
| Total Tests   | 104   |
| Passed Tests  | 104   |
| Failed Tests  | 0     |
| Skipped Tests | 0     |

### Coverage Summary

| Coverage Type | Percentage |
|---------------|------------|
| Lines         | 95%        |
| Branches      | 75%        |
| Method        | 96%        |
| Class         | 100%       |

## Postman Collection

For easy testing and interaction with the API, you can use the Postman collection containing all necessary requests.

### Usage

1. Open Postman and import the [Bokking_App.postman_collection.json](src/main/resources/postman/Booking_App.postman_collection.json).
2. Navigate to the imported Accommodation Booking Service collection.
3. Execute the necessary requests using the appropriate methods and parameters.

> **Note:** Before using the requests, ensure that your local server is running, and you have access to the database.

## Video Demonstration

- Watch a [Loom Video](https://www.loom.com/share/3f1840b2718641c2874d44f6e77dc983?sid=6fb2c9d2-f3a2-4e90-9930-e3bf8da12e19) to see how the project works.

## Happy Coding!
---
