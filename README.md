![book-store-logo](book-store-logo.jpg)

📚 The "Book Store" project is an online platform designed for book enthusiasts. Users can browse an extensive selection of books in various genres, read descriptions, learn about authors and find prices.

The service includes a convenient book search by categories for quick access to literary works. Users can create personal accounts to track their purchase history. Adding books to the cart and completing orders is straightforward and secure, providing a hassle-free shopping experience.
## Technologies Used in the project

- Backend: Spring Framework (Spring Boot, Spring Security, Spring Data JPA), JWT, MySQL, Mapstruct, Jackson
- Build Tool: Maven
- Database Migration: Liquibase
- Containerization: Docker
- Testing: JUnit, Mockito, Test Containers
- API Documentation: Swagger

## Layered Architecture
- **Controller Layer**: Handles incoming HTTP requests and communicates with clients. Controllers contain methods that process these requests and call the necessary services to execute business operations.
- **Service Layer**: Encapsulates the business logic of the application. This layer manages data processing and interaction with repositories and mappers to fulfill business requirements.
- **Repository Layer**: Manages direct interactions with the database. Repositories are responsible for querying, saving, updating, and deleting data as dictated by the application's needs.
- **Security Layer**: Oversees authentication and authorization processes. This layer ensures secure access to resources by implementing role-based access control mechanisms.
- **DTO** (Data Transfer Object) Layer: Consists of objects designed for transferring data between different layers of the application, streamlining data communication by including only necessary information.
- **Mapper Layer**: Facilitates the conversion between different object models, specifically between entities and Data Transfer Objects (DTOs), ensuring data consistency across layers.
- **Test**: Encompasses unit and integration tests that validate the functionality and correctness of various application components, including controllers, services, and repositories.

### Database entities
- **Book**: Represents a literary work with associated metadata such as title, author and other pertinent details.
- **Category**: A classification system for organizing books into various genres or subjects.
- **Shopping Cart**: A temporary repository for products selected by a user for potential purchase, containing itemized details.
- **Cart Item**: An individual entry within a shopping cart that specifies a particular book and its quantity.
- **User**: An individual with a registered account on the platform, encompassing personal details and assigned permissions.
- **Role**: A set of permissions assigned to a user, determining their access level and capabilities within the system, such as USER or ADMIN.
- **Order**: A record of a completed transaction, capturing details of the purchase, including items bought and delivery specifics.
- **Order Item**: A component of an order that details the specific book purchased and the number of units bought.

### User
Public access
- **POST**: /api/auth/registration - Customers can sign up by providing their email, password.
- **POST**: /api/auth/login - Registered users can securely log in and receive their JWT token.
### Book

Only for logged users:
- **GET**: /api/books - Users have the ability to view the catalog of available books.
- **GET**: /api/books/{id} - Users can access detailed information about individual books.
- **GET**: /api/books/search - Users can search for books using various criteria.

Only for the administrator:
- **POST**: /api/books - Admins can add new books to the collection.
- **PUT**: /api/books/{id} - Admins can modify the details of existing books.
- **DELETE**: /api/books/{id} - Admins can remove books from the catalog.
### Category

Only for logged users:
- **GET**: /api/categories - Users can view the list of all available categories.
- **GET**: /api/categories/{id} - Users can get detailed information about specific categories.
- **GET**: /api/categories/{id}/books - Users can see all books that belong to a particular category.

Only for the administrator:
- **POST**: /api/categories - Admins can create new book categories.
- **PUT**: /api/categories/{id} - Admins can update information about existing categories.
- **DELETE**: /api/categories/{id} - Admins can delete categories from the list.
### Shopping Cart

Only for logged users:
- **GET**: /api/cart - Users can see what is in their shopping cart.
- **POST**: /api/cart - Users can add books to their cart.
- **PUT**: /api/cart/cart-items/{id} - Users can change the quantity of items in their cart.
- **DELETE**: /api/cart/cart-items/{id} - Users can remove items from their cart.
### Order

Only for logged users:
- **GET**: /api/orders - Users can view their order history.
- **GET**: /api/orders/{id}/items - Users can see all items in a specific order.
- **GET**: /api/orders/{orderId}/items/{itemId} - Users can get details of a particular item within an order.
- **POST**: /api/orders - Users can place an order for books in their cart.

Only for the administrator:
- **PATCH**: /api/orders/{id} - Admins can update the status of orders.

## SQL Database Diagram
Below is a representation of the database used in the project:

![bookstore-db-diagram](bookstore-db-diagram.png)

## Video Presentation

For a visual demonstration of how the Bookstore API works and its various functionalities, you can watch the video presentation available [here](https://www.loom.com/share/d78e0aa910ba43f188563fdda338b87a).

## How to run Book Store API
1. Download and install [Docker](https://www.docker.com/products/docker-desktop/), [Maven](https://maven.apache.org/download.cgi), [JDK Development Kit](https://www.oracle.com/pl/java/technologies/downloads/).
2. Clone the project [git repository](https://github.com/QbaSekowski/spring-book-app.git).
3. In the .env file you should provide necessary DB and Docker variables, here is an example:
```mysql
MYSQLDB_USER=root  
MYSQLDB_ROOT_PASSWORD=root  
MYSQLDB_DATABASE=test_db  
MYSQLDB_LOCAL_PORT=3307  
MYSQLDB_DOCKER_PORT=3306  
SPRING_LOCAL_PORT=8088  
SPRING_DOCKER_PORT=8080  
DEBUG_PORT=5005
```
4. Run the command `mvn clean package`.
5. Use `docker-compose build` to build Docker container.
6. Use `docker-compose up` to run Docker container.
7. Access the locally running application at http://localhost:8088/api.
   Feel free to test my application using Postman/Swagger.  
   **Postman**: Keep in mind that you have to pass Authorization (Bearer Token) that you receive when logging in.  
   Do you want to test admin features? Here are credentials of sample admin:
   ```json
   {
   "email": "admin@gmail.com",
   "password": "12345678"
   }
   ```
   or perhaps standard user features:
   ```json
   {
   "email": "jurek@wp.pl",
   "password": "qwertyui"
   }
8. To stop and remove containers use `docker-compose down`.

## Possible Improvement

### Payment Integration
Incorporating a payment system, likely utilizing the Stripe API, to enable secure and convenient online payments. This will allow users to effortlessly complete their orders and make purchases directly within the application.

## Final thoughts

This was my first major Java project, and I faced several challenges along the way. While I managed to resolve some issues quickly, others proved to be more complex. The satisfaction of overcoming the toughest problems was particularly rewarding. These challenges enhanced my ability to efficiently search for solutions using available documentation. With the knowledge I've gained, I am confident that I will be able to tackle future obstacles more swiftly.