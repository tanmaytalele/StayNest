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

### Properties
| Column       | Type    | Description                     |
|--------------|--------|---------------------------------|
| id           | Long   | Primary key                     |
| host_id      | UUID   | from keycloak                  |
| description  | Text   | Property description            |
| location     | String |                                 |
| pricePerNight| Long   |                                 |
| imageUrl     | String |                                 |
| maxGuests    | Integer|                                 |
| created_at   | Timestamp|                               |

### Bookings
| Column      | Type   | Description                      |
|-------------|--------|----------------------------------|
| id          | Long   | Primary key                      |
| property_id | Long   | FK -> properties.id              |
| guest_id    | UUID   | from keycloak                   |
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
- **GET /properties** - Search Properties by filters
- **PUT /properties/{id}** - Update property (only provided fields; blocked if future bookings exist)  
 

### Bookings
- **POST /bookings** - Create a booking  
- **PUT /bookings/{id}** - Update booking (dates and guests; defaults to previous values if not provided)  
- **GET /bookings/{id}** - Get booking by ID
- **DELETE /bookings/{id}** - Cancel booking by ID

### Host
- **GET /host/properties** List properties by host
- **GET /host/properties/{id}/bookings** - Bookings for a property
