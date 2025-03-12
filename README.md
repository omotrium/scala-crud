# Profile API Documentation

This project provides a simple API for managing user profiles. The API allows you to create new profiles by sending a JSON payload with the user's details.

## Base URL

http://localhost:10911/api/profiles


### Endpoints

#### POST `/api/profiles`

This endpoint allows you to create a new profile. It expects a JSON payload with the profile details. If the request is valid, it returns the created profile. If the request is invalid, it returns an error message.

##### Request

- **URL**: `/api/profiles`
- **Method**: `POST`
- **Content-Type**: `application/json`

##### Request Body Example:

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

- **name**: The name of the user. (Required)
- **email**: The email address of the user. (Required)


##### Response
- **Status Code 200 (Created)**: The profile was created successfully.
- **Status Code 400 (Bad Request)**: The request body is invalid or the validation fails.
- **Status Code 409 (Conflict)**: The email provided already exists in the system.


#### PUT `/api/profiles/:id`

This endpoint allows you to create a new profile. It expects a JSON payload with the profile details. If the request is valid, it returns the created profile. If the request is invalid, it returns an error message.

##### Request

- **URL**: `/api/profiles`
- **Method**: `POut`
- **Content-Type**: `application/json`

##### Request Body Example:

```json
{
  "name": "John Updated",
  "email": "john.Updated@example.com"
}
```

- **name**: The name of the user. (Required)
- **email**: The email address of the user. (Required)


##### Response
- **Status Code 200 (OK)**: The profile was created successfully.
- **Status Code 400 (Bad Request)**: The request body is invalid or the validation fails.
- **Status Code 404 (Not Found)**: The profile with the specified ID does not exist.
- **Status Code 409 (Conflict)**: The email provided already exists in the system.


#### GET `/api/profiles/:id`

This endpoint allows you to retrieve a profile by its ID.

##### Request

- **URL**: `/api/profiles/:id`
- **Method**: `GET`
- **Content-Type**: `application/json`

##### Request Parameters:
id: The ID of the profile you want to retrieve.

##### Response
- **Status Code 200 (OK)**: The profile was found and returned successfully.
- **Status Code 404 (Not Found)**: The profile with the specified ID does not exist.


#### DELETE `/api/profiles/:id`

This endpoint allows you to retrieve a profile by its ID.

##### Request

- **URL**: `/api/profiles/:id`
- **Method**: `DELETE`
- **Content-Type**: `application/json`

##### Request Parameters:
id: The ID of the profile you want to delete.

##### Response
- **Status Code 200 (OK)**: The profile was deleted successfully.
- **Status Code 404 (Not Found)**: The profile with the specified ID does not exist.