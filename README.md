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
