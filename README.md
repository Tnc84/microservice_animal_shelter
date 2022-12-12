
#    The Animal Shelter application is developed in Java 17 and follows a microservice architecture. With this application, you can create a database of animals for one shelter, as well as a database for customers who need to register for authentication. Technologies%Tools used: Java 17/Spring(Record, Lombok, Mapstruct), MySQL. 
#    For connecting them, I have created five microservices micro_as_animal, micro_as_shelter, and micro_as_user, naming-server-as, and api-gateway-as.Technologies%Tools used: Openfeign, Netflix-eureka, sleuth, Zipkin, RabbitMQ, Resilience4j.

#   For running the project must clone all the microservices.
    
	eureka server: localhost:8761

#  animal microservice port: 
	8093 / 8095 
	ex: http://localhost:8093/animals/getAll

#   shelter microservice port:
	8092 / 8094
        ex: localhost:8092/getAll -for get all shelters
            localhost:8092/getAlAnimals -for getting all the animals from the shelter

# api-gateway port:
	8765
	ex: http://localhost:8765/shelter-microservice/shelters/getAllAnimals
	    http://localhost:8765/animal-microservice/animals/getAll

