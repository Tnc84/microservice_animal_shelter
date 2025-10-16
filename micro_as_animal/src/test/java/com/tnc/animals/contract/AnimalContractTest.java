package com.tnc.animals.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

/**
 * Contract tests for Animal microservice
 * Tests API contracts between services using Pact
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Provider("animal-service")
@PactFolder("pacts")
@ExtendWith(PactVerificationInvocationContextProvider.class)
@DisplayName("Animal Contract Tests")
class AnimalContractTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AnimalRepository animalRepository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
        animalRepository.deleteAll();
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("animals exist")
    void animalsExist() {
        // Create test animals for the contract
        Animal animal1 = new Animal();
        animal1.setName("Buddy");
        animal1.setSpecies("Dog");
        animal1.setBreed("Golden Retriever");
        animalRepository.save(animal1);

        Animal animal2 = new Animal();
        animal2.setName("Whiskers");
        animal2.setSpecies("Cat");
        animal2.setBreed("Persian");
        animalRepository.save(animal2);
    }

    @State("animal with id 1 exists")
    void animalWithId1Exists() {
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName("Buddy");
        animal.setSpecies("Dog");
        animal.setBreed("Golden Retriever");
        animalRepository.save(animal);
    }

    @State("no animals exist")
    void noAnimalsExist() {
        animalRepository.deleteAll();
    }

    @State("animal with id 999 does not exist")
    void animalWithId999DoesNotExist() {
        // Ensure no animal with ID 999 exists
        animalRepository.deleteAll();
    }
}
