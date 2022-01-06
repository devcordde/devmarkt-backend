# 💸 Devmarkt-Backend

Devmarkt Backend of the Devmarkt-Rework

## Goal of this project

This project stands in connection with the devmarkt rework, which is a full rework of the current
devmarkt created by the devcord discord server.

This rework of its backend want to be more feature-rich and more maintainable.

## Features and structure

The Devmarkt-Backend is divided into 4 main parts:

System.out.println("Bla");

- [Templates](#templates)
- [Applications](#applications)
- [History](#history)
- [Management](#management)

### Templates

Templates are a collection of questions, identified by a unique name.

### Applications

Applications are answered templates by a user.

#### Application Service

The application service (endpoints and event) is a service, that stores the current application of a
user. This application is either waiting for processing by a mod or is in revision mode.

If the application is accepted, it will move to the history and will be immutable.

### History

The History stores all application of a user, identified by its timestamp.

It also contains actions to delete an application, but this is not guaranteed to be done on client
side.

### Management

The management service provides some admin actions like:

- fetch/delete all data of a user
- block/unblock a user from creating application

## Usage

soon...

## Module information

The `backend` module contains the main application of this backend. The `dto_common` module contains
some DTOs for the Rest API and events.

## Contributing

Before you starts to write your code, you should open an issue so that we can discuss your idea. The
issue will also use to track the current status of your implementation, in addition you should open
a draft PR, when you're starting to work on it.

## Pull requests and squashing

We usually don't squash PRs unless the commit messages are not meaningful. Therefore, we really
recommend that you always use useful commit messages.

### Styleguide

We use the Google [Java Style Guide](https://google.github.io/styleguide/javaguide.html), if you use
IntelliJ IDEA the style Guide should be applied automatically.

## License

The project is published under
the [Apache License 2.0](https://github.com/devcordde/devmarkt-backend/blob/main/LICENSE), a
copyright header is provided with IntelliJ IDEA.
