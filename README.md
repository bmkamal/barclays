About
This is a demo for Eagle Bank Demo App.

Getting Started
Follow these instructions to get the application running on your local machine.

Prerequisites
JDK 17 or later installed.
Apache Maven installed.

Build the project with Maven:
mvn clean install
Run the application:
java -jar target/eagle-bank-api-0.0.1-SNAPSHOT.jar
The API will start on http://localhost:8080.

API Endpoints
The API provides a set of endpoints for managing users, accounts, and transactions. 
Authentication: POST /v1/auth/token
Users: POST, GET at /v1/users
Bank Accounts: POST, GET at /v1/accounts
Transactions: POST, GET at /v1/accounts/{accountNumber}/transactions
H2 Database Console
For development purposes, the in-memory H2 database console is enabled and accessible at:

URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:eagledb
Username: sa
Password: password

Create a User: Use the POST /v1/users request first. This is a public endpoint.
Get the userId: Copy the id from the creation response and set it as the userId collection variable in Postman.
Login: Use the POST /v1/auth/token request. The access token will be automatically saved to a collection variable.

Note: Since the H2 database is in-memory, you must create a new user and log in again every time you restart the application.
