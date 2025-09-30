# StayNest Backend

## Overview
StayNest is a backend platform similar to Airbnb where users can list properties, book stays, and manage reservations. The application uses Spring Boot and Keycloak for authentication.

## Setup

### Configure Application
Edit `src/main/resources/application.yml` and set your database and Keycloak credentials.


### Start the Application
```bash
mvn spring-boot:run
```

### Keycloak Setup
- Create realm: `StayNest`
- Add client: `staynest-backend` (confidential)
- Create roles: `HOST`, `GUEST`

## Database Schema

### Users
| Column     | Type   | Description           |
|------------|--------|-------------------|
| id         | UUID   | Primary key         |
| username   | String | Unique              |
| email      | String |                     |
| password   | String | Encrypted           |
| roles      | String | Comma-separated roles|
| created_at | Timestamp |                  |

### Properties
| Column       | Type    | Description                     |
|--------------|--------|---------------------------------|
| id           | UUID   | Primary key                     |
| host_id      | UUID   | FK -> users.id                  |
| description  | Text   | Property description            |
| location     | String |                                 |
| pricePerNight| Decimal|                                 |
| imageUrl     | String |                                 |
| maxGuests    | Integer|                                 |
| created_at   | Timestamp|                               |

### Bookings
| Column      | Type   | Description                      |
|-------------|--------|----------------------------------|
| id          | UUID   | Primary key                      |
| property_id | UUID   | FK -> properties.id              |
| guest_id    | UUID   | FK -> users.id                   |
| start_date  | Date   |                                  |
| end_date    | Date   |                                  |
| guests      | Integer| Number of guests                 |
| created_at  | Timestamp|                               |

## API Endpoints

### Users
- **POST /users/register** - Register a new user  
- **POST /users/login** - Login via Keycloak  

### Properties
- **POST /properties** - Create a property  
- **GET /properties/{id}** - Get property by ID  
- **PUT /properties/{id}** - Update property (only provided fields; blocked if future bookings exist)  
- **DELETE /properties/{id}** - Delete property  

### Bookings
- **POST /bookings** - Create a booking  
- **PUT /bookings/{id}** - Update booking (dates and guests; defaults to previous values if not provided)  
- **GET /bookings/{id}** - Get booking by ID
