# Smart Order & Inventory Management System
A full-stack enterprise web application for managing orders, inventory, billing, and users using Spring Boot microservices and Angular, fully Dockerized with centralized configuration.

## Project Overview
This project implements a modern **Order & Inventory Management System** following real-world enterprise practices such as:
- Microservices architecture
- Role-based access control
- Secure REST APIs
- Event-driven communication using RabbitMQ
- CI/CD pipeline using Jenkins
- Code quality analysis using SonarCloud
- Fully Dockerized deployment

## Project Architecture
![ARCHITECTURE_DIAGRAM](https://github.com/user-attachments/assets/d8640726-0127-4302-8337-6d0dd13075bc)

## Database Design - ER Diagram
<img width="1730" height="593" alt="ER Diagram" src="https://github.com/user-attachments/assets/f7b0f8a7-7bfd-4d0e-953d-aa495bdb16cd" />

## Tech Stack

- **Backend:** Spring Boot Microservices (Parent Maven POM)  
- **Frontend:** Angular
- **Database:** MySQL
- **API Gateway:** Spring Cloud Gateway  
- **Service Discovery:** Eureka Server  
- **Config Management:** Spring Cloud Config Server  
- **Messaging:** RabbitMQ (Email Service)  
- **Security:** JWT-based Authentication & RBAC  
- **CI/CD:** Jenkins Pipeline  
- **Code Quality:** SonarCloud  
- **Deployment:** Docker & Docker Compose

## UI Screenshots

Refer to the ```FRONTEND SCREENSHOTS.pdf``` in root for detailed ui.

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/b8cae276-5f44-4fbb-8428-dbde8c1bdb1d" />

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/f02df878-e774-4f73-b88a-55f16e8eb4ab" />

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/fcd973b1-81ac-402b-ad40-cd445c508dec" />

### Email

<img width="1420" height="586" alt="image" src="https://github.com/user-attachments/assets/343775bd-8b07-49f5-8b2e-ce857a488128" />

## Setup & Installation

### Prerequisites

Ensure the following are installed:
- Java 17+
- Maven 3.8+
- Node.js 18+
- Angular CLI
- Docker & Docker Compose
- Git

Verify installations:
```bash
java -version
mvn -version
node -v
ng version
docker --version
docker-compose --version
  ```

### **Step 1: Clone the Repository**
```bash
git clone https://github.com/isananya/inventory-mgmt.git
cd backend
```

### **Step 2: Build Backend (Parent POM)**
```bash
mvn clean package
```

### **Step 3: Start Backend Services (Dockerized)**
```bash
docker-compose up --build
```

### **Step 4: Frontend Setup**
```bash
cd frontend
npm install
ng serve
```

## Service Access URLs

- **Eureka Dashboard:**
```bash
http://localhost:8761
```

- **API Gateway:**  
```bash
(http://localhost:8765)
```

- **RabbitMQ Management Console:**
```bash
(http://localhost:15672)
```

- **Frontend:**
```bash
(http://localhost:4200)
```

## Run Tests
```bash
cd backend
mvn test
```


## API Endpoints

| Method | Endpoint | Access Role(s) | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | | | |
| `POST` | `/auth/signup` | Public | Register a new user |
| `POST` | `/auth/login` | Public | Login (Returns JWT) |
| `POST` | `/auth/logout` | Authenticated | Logout (Invalidate cookie) |
| `GET` | `/auth/profile` | Authenticated | Get current user profile |
| `PUT` | `/auth/password` | Authenticated | Change password |
| `DELETE` | `/auth` | Authenticated | Delete own account |
| **Users** | | | |
| `GET` | `/users` | ADMIN | Get all users |
| `DELETE` | `/users/{id}` | ADMIN | Delete a user by ID |
| **Category** | | | |
| `GET` | `/category` | Public | Get all categories |
| `POST` | `/category` | ADMIN | Add a new category |
| **Products** | | | |
| `GET` | `/products` | Public | Get all products (Paginated) |
| `POST` | `/products` | ADMIN | Add a new product |
| `GET` | `/products/{id}` | Public | Get product by ID |
| `PATCH` | `/products/{id}` | ADMIN | Update product properties |
| `GET` | `/products/category/{catId}` | Public | Get products by Category |
| `GET` | `/products/search/{name}` | Public | Search products by name |
| **Inventory** | | | |
| `POST` | `/inventory` | WAREHOUSE_MANAGER, ADMIN | Add inventory item |
| `GET` | `/inventory/product/{prodId}` | CUSTOMER, SALES, WAREHOUSE, ADMIN | Get inventory by Product ID |
| `GET` | `/inventory/warehouse/{whId}` | WAREHOUSE_MANAGER, SALES, ADMIN | Get inventory by Warehouse ID |
| `PATCH` | `/inventory/{id}` | WAREHOUSE_MANAGER, ADMIN | Update inventory quantity |
| `PUT` | `/inventory/stock/add` | WAREHOUSE_MANAGER, ADMIN | Bulk add stock |
| `PUT` | `/inventory/stock/deduct` | WAREHOUSE_MANAGER, ADMIN | Bulk deduct stock |
| `GET` | `/inventory/low-stock` | WAREHOUSE_MANAGER, ADMIN | Get low stock items |
| `POST` | `/inventory/check` | CUSTOMER, SALES, WAREHOUSE, ADMIN | Check stock availability |
| **Warehouse** | | | |
| `GET` | `/warehouse` | WAREHOUSE_MANAGER, SALES, ADMIN | Get all warehouses |
| `POST` | `/warehouse` | ADMIN | Add a new warehouse |
| `GET` | `/warehouse/active` | WAREHOUSE_MANAGER, SALES, ADMIN | Get active warehouses |
| `GET` | `/warehouse/{id}` | WAREHOUSE_MANAGER, SALES, ADMIN | Get warehouse by ID |
| `PUT` | `/warehouse/{id}` | ADMIN | Update warehouse |
| `PATCH` | `/warehouse/{id}/deactivate` | ADMIN | Deactivate warehouse |
| `PATCH` | `/warehouse/{id}/activate` | ADMIN | Activate warehouse |
| **Order** | | | |
| `POST` | `/order` | CUSTOMER, SALES, ADMIN | Place a new order |
| `GET` | `/order` | ADMIN | Get all orders |
| `GET` | `/order/{id}` | CUSTOMER, SALES, WAREHOUSE, FINANCE, ADMIN | Get order by ID |
| `GET` | `/order/customer/{custId}` | ADMIN | Get orders by Customer |
| `PUT` | `/order/{id}/cancel` | CUSTOMER, SALES, ADMIN | Cancel order |
| `GET` | `/order/{id}/status` | CUSTOMER, SALES, WAREHOUSE, FINANCE, ADMIN | Get order status |
| `PATCH` | `/order/{id}/status` | WAREHOUSE_MANAGER, ADMIN | Update order status |
| `GET` | `/order/{id}/items` | CUSTOMER, SALES, WAREHOUSE, FINANCE, ADMIN | Get order items |
| **Billing** | | | |
| `GET` | `/billing` | FINANCE_OFFICER, ADMIN | Get all invoices |
| `GET` | `/billing/{id}` | CUSTOMER, SALES, FINANCE, ADMIN | Get invoice by ID |
| `POST` | `/billing/order/{orderId}` | CUSTOMER, SALES, ADMIN | Generate Invoice for Order |
| `GET` | `/billing/order/{orderId}` | CUSTOMER, SALES, FINANCE, ADMIN | Get invoice by Order ID |
| `PATCH` | `/billing/order/{orderId}/status`| CUSTOMER, SALES, ADMIN | Update invoice status |

## Sonar Analysis

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/f33ac387-6d3c-40f9-8178-cfb91bc15dc5" />

