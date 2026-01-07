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
