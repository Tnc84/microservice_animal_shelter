# User Microservice

This is a microservice that contains specific information for register and login.
___
The person will have id, first name, last name, and email for registration.

### Example
user id [1L]() first name [John]() last name [Smith]() email [johnsmith@email.com]()

if you have spring cloud config server in pom you must define in application.yaml spring.  config:
import: optional:configserver:http://localhost:8888