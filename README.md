# 💸 Devmarkt-Backend

Devmarkt Backend of the Devmarkt-Rework

## Goal of this project

This project stands in connection with the devmarkt rework, which is a full rework of the current
devmarkt created by the devcord discord server.

This rework of its backend want to be more feature-rich and more maintainable.

### Usage of "backend"

The term "backend" is to be understood as "Devmarkt Backend".

## Features

The backend allows clients to work with templates, application and users.

### Templates

Templates are collections of questions, identified by a unique name. They can be deleted or created.
If a template is deleted, then it stays in the database, so that application which are based on
this template, are still understandable. 
Attention: Template names that are retrieved from the application are not necessarily existing templates
or are related to the applications answers. Therefore, templates that belong to applications
should always be fetched in connection with them.

### Applications

Applications are user filled Templates, each of them is based on a template, 
belongs to a user and contains a status, that is either "ACCEPTED", "REJECTED" or "UNPROCESSED".
Old applications can be retrieved via their timestamp of process and the belonging user.

Applications can be created, processed or deleted. 

#### Application creation

When an application is created, then its stored as the current application with the status "UNPROCESSED"
until its deleted or processed. A user can also have one current (unprocessed) application.

#### Application processing

When an application is processed it's either accepted or rejected.
In the case of an acceptation the application is stored under its timestamp, user 
and an "ACCEPTED" status, also an event is fired to notify clients, that an application is processed.

In the case of a rejection, the application remains as the current application, until it's deleted.
Also, an event is fired to notify clients and the application status is set to "REJECTED".

#### Application deletion

Both processed and unprocessed aka current applications can be deleted.
In the case of a deletion, the application is completely removed from the backend except the logs
and clients are notified. **However, we cannot guarantee that clients delete an application as well.**

### Users

Users are commonly used in this backend. Their task is to represent a unique identity.
A user is identified by an id which consists of a type and id.
The type is a string, which represent the type of this id to avoid conflicts.
The id is a 64 bit long signed integer, often named long or int64.
Together, they represent a unique identifier and should be treated as such.
Users aren't always be real persons.

The type "internal" is reserved by the backend and can't be used to create or modify users.

## The GraphQL API

Different from most web services, this backend uses a GraphQL api and no Rest api.
The api can be accessed via the "/graphl" endpoint, which is actually a rest endpoint
and always return with 200 - ok. The graphql query must be passed in the http body.
For experiments, we also provide a GraphiQL endpoint: "/graphiql".
The api documentation can also be read there.

### GraphQL Errors

A GraphQL error is an error in the "error" section of the response.
We see a GraphQL error as an identifier that something went wrong during the execution
of a query. In general if any error occurs, the data field will be "null".
The api provide following graphql errors:

- UnauthorizedError (Classification: "UNAUTHORIZED") - if an invalid "Authorization" header was found,
including invalid jwt tokens

- Forbidden Error (Classification: "FORBIDDEN") - if the user is unauthorized to do something, 
the error messages contain the missing permissions

Please note that the graphql java implementation provide errors by their owm, they can also be returned.
In addition, we use [graphql-java-extended-validation](https://github.com/graphql-java/graphql-java-extended-validation)
to validate user input. The validation will return a validation error and null value if it fails.

## Authorization and Authentication

To make authorization simple and comfortable, we decided to use the http "Authorization" header and a
jwt token based authorization. We provide 2 authorization method: Self and Foreign, more about this later.

### The token

Each jwt token belongs to a user, that's id is stores in the "sub" jwt claim in a "type:id" format.
The id is represented as a decimal number and the type as a simple string.
In addition, the token can hold an "exp" claim, which marks the expiration date of this token.

### Roles

We use a simple role system for authorization. The backend provides a user and an admin role.
The admin role is allowed to use every query and a standard admin user is created,
that can't be modified in any way.

To roles required to use a field are indicated by an "@Auth" directive in the GraphQL schema.

### Authorization header and methods

The header format is oriented on the http "Authorization" format.
We provide 2 authorization methods: Self and Foreign.

#### Self authorization

The "Self" method takes only the jwt token as an argument, this token is used
to identify the current user. If the user isn't found, then the api respond with a "UNAUTHORIZED".

#### Foreign authorization

The "Foreign" method takes 2 argument, a sudoer jwt token and a user id (in the "type:id" format).
The user id is separated by a space.
Example: `Foreign eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnRlcm5hbDoxIiwiaWF0IjoxNTE2MjM5MDIyfQ.CroFo1WLY0i5T_HpS0jIuVQCbO46Pie8jID93b2COk4 internal:1`

The sudoer must have the "admin" role and the passed user id is used to authorize and authenticate this query.
If the passed user isn't found, then the backend creates a new user, that has the "user" role by default.
// TODO: implement this| If the sudoer isn't found or don't have the admin role, then the api responds with a "UNAUTHORIZED".

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
