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

- UnauthorizedError (Classification: "UNAUTHORIZED") - if an invalid or no jwt token was found

- Forbidden Error (Classification: "FORBIDDEN") - if the user is unauthorized to do something, 
the error messages contain the missing permissions

Please note that the graphql java implementation provide errors by their owm, they can also be returned.
In addition, we use [graphql-java-extended-validation](https://github.com/graphql-java/graphql-java-extended-validation)
to validate user input. The validation will return a validation error and null value if it fails.

## Authorization and Authentication

Since we use GraphQL we cannot handle them like normal http auth. 
So we decided to build our own system here.

### Authentication

For the authentication part we decided to use a jwt, with a "sub" and "iat" claim.
The "sub" claim contains a user id in the following format: "type:id". The id is passed in a decimal
string representation.
The "iat" claim is not always needed, in case of missing claim the jwt token
has no expiration date.
The token must be passed by a variable called "Authorization" in a string format.

### Authorization

For the authorization we decided to use a permission system based on roles.
Permissions belong to roles and roles to users, so users actually don't have permission directly.
The api contains endpoints for user roles and role permissions.
So a user with the given permissions are able to control permissions.
This permission should only be granted to known and trusted users, since this allows
users to grant themselves permissions and roles.
The backend also provide a default admin user and role, the permissions of the admin role 
and the admin users roles can't be modified.
The admin user has the id "internal:1", the jwt token must be generated manually.

If a user hasn't all permission to execute a query, then nothing of this query is executed
and the "error" section of the response contains unauthorized permissions.

### Permission Generation

Permission are generated from the GraphQL schema and query.
Permissions are grouped in 3 types, called operation, which are based on graphql operations:
QUERY, MUTATION, SUBSCRIPTION

They're simple dot separated strings and each node (dot separated value) is either a field
or union member. Fields always began with a lower case letter 
and union members always with an upper letter. Union member are (object) types, so the permission contains
the name of this type followed by its fields. If a field in a type is member of a parent interface,
then the name of this interface is used.

Let's image following schema:
```graphql
type Query {
    rootOperation: Response!
}

union Response = Fail | Success

type Fail {
    errorCode: String!
}

type Success {
    field1: String!
    field2: SomeType!
}

type SomeType {
    someField1: Int!
    someField2: Boolean
}
```

The generated permissions are now:
- `QUERY: rootOperation.Fail.errorCode`
- `QUERY: rootOperation.Success.field1`
- `QUERY: rootOperation.Success.field2.someField1`
- `QUERY: rootOperation.Success.field2.someField2`

This allows to control what users are allowed to on a field level.
Let's take a look on a query:
```graphql
query {
    rootOperation {
        ... on Fail {
            errorCode
        }
        ... on Success {
            field2 {
                someField1
            }
        }
    }
}
```
The generated permissions are:
- `QUERY: rootOperation.Fail.errorCode`
- `QUERY: rootOperation.Success.field2.someField1`

We note that this are exact the permissions, which we generated before from the schema.
Please note that the "operation: permission" format is not the actual permission format.
In the graphql api, permission are a type that consists of "operation" and "query" fields.
"operation" is here our dot separated permission (eg. `rootOperation.Fail.errorCode`) and "operation"
our "QUERY", which is member of an enum type "Operation".

#### Introspections
Introspections haven't real permissions, root level introspections such as `__schema` are allowed to all users.
When it comes to type introspections e.g. `__typename` then the user need at least one parent permission.

Let's look at an example query:
```graphql
query {
    rootOperation {
        ... on Fail {
            __typename
        }
    }
}
```

We remind that rootOperation has these permissions:
- `QUERY: rootOperation.Fail.errorCode`
- `QUERY: rootOperation.Success.field1`
- `QUERY: rootOperation.Success.field2.someField1`
- `QUERY: rootOperation.Success.field2.someField2`

our interest is only: `QUERY: rootOperation.Fail.errorCode`.
We see in the query there isn't a field errorCode here, so you maid aspect there's no permissions generated.
But in reality there is: `rootOperation.Fail.__typename` and the backend now looks for a permission that starts with:
`rootOperation.Fail` the introspection (`__typename`) is removed. If the user now have a permission that starts with
`rootOperation.Fail` the query is executed. For this example the permission would be:
`rootOperation.Fail.errorCode` because there's only one.

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
