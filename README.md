# Messaging REST API

## Description

This is a backend for a Messaging application.

A message is a text sent from user A to user B. Users have nicknames and are identified by unique ids.
To simplify - you do not need to implement any sort of authentication. You can impersonate as any user just by providing
his id in the request header.
Implement the following user stories as HTTP API following REST principles.

### Requirements, what were implemented

1. As a non-user, I can create my account by providing my nickname (nicknames should be unique)
2. As a user, I can send a message to another user identified by his id (you cannot send a message to yourself)
3. As a user, I can view all messages that I received
4. As a user, I can view all messages that I sent
5. As a user, I can view all messages received from a particular user
6. All messages should be persisted in PostgreSQL.
7. Messages are sent as events using RabbitMq Broker

## How To:

### Dependencies

1. Java 17
2. Maven
3. docker-compose
4. Web Browser

Here is used Swagger UI (http://localhost:8080/swagger-ui/index.html), but also a tool like POSTMAN can be used.

To install docker on MacOS:

```
brew install --cask docker
```

NOTE: launch docker after it was installed.

### Steps to run

1. Execute command in project directory to init DB and start RabbitMq: `docker-compose up`
2. Run the application: `mvn spring-boot:run`
3. Open the page: http://localhost:8080/swagger-ui/index.html

### TESTS

Run unit-tests:

```
mvn clean test
```

### NOTES FROM DEVELOPER

1. All requirements were implemented. You can see all available endpoints on the Swagger UI
   page (http://localhost:8080/swagger-ui/index.html).
2. Swagger UI is used, but no full potential of it in documenting API was used here. It is used more to show the
   structure
   view and substitute tools like CURL or POSTMAN. It could have had in REST Controllers more information
   via `@Operation`
   and `@ApiResponse` and so on.
3. There is no `TIMESTAMP` in the response to get messages. Unfortunately I forgot to add it in the beginning, and
   eventually decided not to, cause it would require to write more tests as well. However, I added `DEFAULT_TIMESTAMP`
   to the `messages` table. See `V2__create_messages_table` migration.
4. If there is a fail during on event (our text message) received in `@RabbitListener` in `MessagesReceiver` (or
   validation error), the event will end up in dead letter queue (DLQ), so it can be reprocessed later. However, the
   reprocessing was not implemented. How I see it: it should either be resent later after timeout, or manually.
5. When event (our text message) is published to the Queue in `MessagesPublisher`, it can be done more reliable if the
   message itself is saved in a separate table in JSON column to preserve it in the case when a broker is not available.
   So later the event can be sent again once the broker is back.
6. I added a custom exception `UserNotFoundException` and throw it in case when there is no sender or recipient (
   in `MessagesSendService` and `MessagesReceiveService` services).
   However, I could have avoided that, and rely on DB error, because there is `FK Constraint` in DB between `messages`
   and `users` tables. I could just catch `DataIntegrityViolationException` or similar and handle it
   in `@ControllerAdvice` Service. But for me throwing `UserNotFoundException` is more expected behaviour.
7. It could have been more and better logging, I added logging in some places, but just to show, that I remember how
   logging is important.