# Getting Started

## Prerequisites

Depending on how you want to run the application, ensure you have the following installed:

### If running with Docker

* [Docker](https://www.docker.com/get-started/)
* [Docker Compose](https://docs.docker.com/compose/install/)

### If running with Maven (Without Docker)

* [Java 21](https://www.oracle.com/tr/java/technologies/downloads/#java21)
* [Maven](https://maven.apache.org/install.html)

## Running the Application

#### 1. Clone the Repository

```
git clone <repository-url>
cd <repository-folder>
```

---

### 2. Running with Docker Compose

`docker compose up --build`

This command:

* Builds the application
* Runs the application inside a Docker container

---

### 3. Running with Maven (Without Docker)

If you want to run the application without Docker, use the following commands:

#### Build the Application

`mvn clean package`

This will create a .jar file inside the target/ directory.

#### Run the Application

`java -jar target/*.jar`

---

### 4. Accessing the Application

Once the application is running, you can access it at:

* **API Endpoint:** http://localhost:8080
* **H2 Database Console:** http://localhost:8080/h2-console

#### H2 Database Credentials

* **JDBC URL:** jdbc:h2:mem:brokeragedb
* **Username:** root
* **Password:** toor

#### Initial Admin User Credentials

* **username:** admin
* **password:** admin

  > You can find the postman collection here: [Brokerage.postman_collection.json](https://github.com/user-attachments/files/19274277/Brokerage.postman_collection.json)
