# Vending Machine REST API

This is a simple RESTful API for a vending machine, implemented using Spring Boot and Spring Security. The API is stateless and uses JWT for authentication and authorization.

The API has the following endpoints:

### POST /user
Creates a new user. The user can be either a buyer or a seller.

Request body:
```json
{
    "username": "string",
    "password": "string",
    "role": "string"
}
```

The role field must be either "BUYER" or "SELLER".

### POST /login
Authenticates a user and returns a JWT token.

Request body:
```json
{
    "username": "string",
    "password": "string"
}
```

### POST /deposit
Deposits money into the buyer's account. Requires a buyer role to access.

Request body:
```json
{
    "amount": "integer"
}
```

### POST /product
Creates a new product. Requires a seller role to access.

Request body:
```json
{
    "name": "string",
    "cost": "integer",
    "amountAvailable": "integer"
}
```


### POST /buy
Allows a buyer to purchase a product. Requires a buyer role to access.

Request body:
```json
{
    "productId": "long",
    "quantity": "integer"
}
```
