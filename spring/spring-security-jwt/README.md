# Simple SpringBoot security JWT demo

Really simple spring security demo. This demo application has following REST end-points:

* __security__ - login / logout.
* __protected data__ - accessible only after login for clients with valid JWT.
* __public data__ - accessible to any client, no login required.

Public data is open, accessible without login. Protected data is accessible only after login. 
After login, each request must include ``Authorization: Bearer <token>`` field in http header.
This field contains [JWT](https://tools.ietf.org/html/rfc7519) token issued by login action. 

* __Authentication__ is handled by internal service ``itx.examples.springboot.security.springsecurity.jwt.services.UserAccessService``
* __Authorization__ is handled by Spring's Method Security, RBAC model is used.

### Users, Passwords and Roles
Following user / password and roles are available in this demo.
* joe / secret, ROLE_USER
* jane / secret, ROLE_USER, ROLE_ADMIN
* alice / secret, ROLE_PUBLIC

[JWT for Java](https://github.com/jwtk/jjwt) is used for JSON Web Token operations.

### Security - Login
Client presents itself with username / password credentials. After credentials match, server 
produces unique key-pair for the client. This keypair is stored in internal server cache and is used to 
issue JWT for the client as well as verify each JWT from same client.
* __POST__ http://localhost:8888/services/security/login
  ```
  {
     "userName": "jane",
     "password": "secret"
  }
  ```
  In case login is successful, user data is returned in response.
  ```
  {
      "userId": {
          "id": "joe"
      },
      "roles": [
          { "id": "ROLE_USER" }, { "id": "ROLE_ADMIN" }
      ],
      "jwToken": {
          "token": <token>
      }
  }
  ```
  jwToken string must be used in http header for each request
  ```
  Authorization: Bearer <token>
  ```
  
### Security - Logout
This action revokes client's certificate from internal server cache, so further verification
of client's JWT is not possible even if client's JWT is technically valid.
* __GET__ http://localhost:8888/services/security/logout

### Get protected data
GET protected data for different user roles:
* __GET__ http://localhost:8888/services/data/users/all (ROLE_USER, ROLE_ADMIN)
* __GET__ http://localhost:8888/services/data/admins/all (ROLE_ADMIN)

### Get public data
* __GET__ http://localhost:8888/services/public/data/all (no login required)

### Build and run
```
gradle clean build 
java -jar build/libs/spring-security-jwt-0.0.1-SNAPSHOT.jar 
```