# Accommodation Booking Service

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

## Features and Functionalities

### Controllers

1. **Authentication Controller**
    - `POST /register`: Allows users to register a new account.
    - `POST /login`: Grants JWT tokens to authenticated users.

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

1. **Clone the Repository**

    ```bash
    git clone https://github.com/4Vitalii5/booking-app.git
    cd booking-app
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

3. **Build and Run the Application**

    ```bash
    mvn clean install
    docker-compose up --build
    ```

4. **Access the Application**

    - The application will be running at `http://localhost:8080`.

## Challenges Faced and Solutions

1. **Real-Time Property Availability**: Implemented a robust database schema and optimized queries to ensure real-time property availability.
2. **Payment Processing**: Integrated Stripe API to handle secure and efficient payment transactions.
3. **Notification System**: Set up a notification system using Telegram API to alert administrators about important events.

## Postman Collection

- You can find a collection of Postman requests [here](./postman/collection.json). Import it into your Postman to easily test all the endpoints.

## Video Demonstration

- Watch a [Loom Video](https://www.loom.com/share/your_video_link) to see how the project works.

## Conclusion

**Happy Coding!**

---
