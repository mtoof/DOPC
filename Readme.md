### Spring boot Restful API

This is my solution for [Wolt 2025 Backend Engineering Internship](https://github.com/woltapp/backend-internship-2025) assignment 2025


I have kept this document as simple as possible and explained it in a straightforward manner.

# Technologies

I have used the below technologies to build this application.

1. Kotlin
2. Spring Boot
3. Java 21

# Wolt DOPC Assignment Structure

```
src+---main
|   +---kotlin
|   |   +---wolt
|   |       +---dopc
|   |           +---client  -> This package contains the client to call the external API
|   |           +---configuration -> This package contains the configuration of the application
|   |           +---controller -> This package contains the controller of the application
|   |           +---dto -> This package contains the data transfer object
|   |           +---Exeption -> This package contains the Global and custom exceptions of the application
|   |           +---service  -> This package contains the service of the application
|   +---resources
|       +---static
|       +---templates
+---test
    +---kotlin
        +---wolt
            +---dopc -> This package contains the service layer unit test and Integration test cases of the application
```

# How to run the application


1. Either you can run the application from the IDE or you can run the application from the command line or Build and package the project or Run the application locally
Please make sure you have installed `Java 21` in your system.

2. If you want to run the application from the command line then you can use the below command.

3. Go to the root directory of the project and run the below command.

4. I have not changed the default port of the application. The application will run on port 8080.

`./gradlew bootRun` -> This command will run the application localy and it will start the application on port 8080.

`./gradlew build` -> This command will build the project, run tests and it will create the jar file in the build/libs directory.

`java -jar build/libs/${File-name}-SNAPSHOT.jar` -> This command will run the jar file and it will start the application on port 8080. Replace the `${File-name}` with the actual file name.



5. Once the application is up and running then you can access the application using the below URL.

6. `http://localhost:8000/api/v1/delivery-order-price`

7. You can also use this query parameter to get the result.

`curl http://localhost:8000/api/v1/delivery-order-price?venue_slug=home-assignment-venue-helsinki&cart_value=1000&user_lat=60.17094&user_lon=24.93087`

8. You can also run the test cases using the below command.
`./gradlew test`  -> This command will run the unit test cases and integration test cases of the application.



# Quick explanation of the code

I created a controller class to handle incoming requests. It calls the service class for results and returns a `ResponseEntity.ok()` response. Exceptions are managed by a global exception handler, which uses a `custom exception` class. Any service exceptions are handled globally and returned as a `ResponseEntity.badRequest()` with an error message. I avoided using a try-catch block in the controller to keep it clean, delegating exception handling to the global handler with customized messages.

The service class manages the business logic, with an injected `HTTP client` to call the external APIs.

DTO package contains the data classes for the request and response objects.

A configuration class manages the RestTemplate bean.

I wrote unit and integration test cases for both the service and controller classes, which can be found at the path below.

```
src+---test
        +---kotlin
            +---wolt
                +---dopc -> This package contains the service layer unit test and Integration test cases of the application
```
