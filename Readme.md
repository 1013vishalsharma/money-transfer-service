# Money-Transfer-Service
A simple restful service to transfer money between two accounts and check details of an account.

# Technologies used
- Java 11
- Maven
- Spring Boot
- H2 In memory DB
- Docker
- Junit, Mockito
- Apache Tomcat

# Installation

### Stand alone deployment
- Clone the repo on your system
- open a terminal or cmd in the directory where you have the project
- run the following command on the prompt: **mvn clean install**
- wait till the build is completed and successful
- run the following command on the prompt: **java -jar target/account-details-service-0.0.1-SNAPSHOT.jar**
- service should be up and running, you can test via postman

### Docker Deployment
- Clone the repository on your system
- open a terminal or cmd in the directory where you have the project
- run the following command on the prompt: **docker build -t money-transfer-service .**
- This will build the image from the dockerfile present in the project
- run the following command on the promp: **docker run -d -p 8080:8080 money-transfer-service**
- This will deploy the image as a container
- service should be up an running, you can test via postman

# API

	GET /actuator/health
to get health of the application

	GET /accounts/{accountNumber}
accountNumber: account number for which you want the details

	POST /transfers
to transfer money between two accounts

More documentation related to API's can be found here
[http://localhost:8080/swagger-ui/swagger-ui/index.html](http://localhost:8080/swagger-ui/swagger-ui/index.html)

To connect to H2 database web based UI
[http://localhost:8080/h2](http://localhost:8080/h2)
- use JDBC URL as **jdbc:h2:mem:testdb**
- use username as **sa**
- keep the password as empty 

# Test & Coverage

Added unit test and integration tests for different layers
- Controller layer tests using WebMvcTest
- DAO layer tests using DataJpaTest
- Service layer tests using Mockito
- End-to-End integration tests using SpringBootTest

Achieved coverage > 90%
