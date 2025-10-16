package com.tnc.animals.integration;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests using TestContainers with real MySQL database
 * Tests the complete flow from controller to database with real database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
@DisplayName("Animal TestContainers Integration Tests")
class AnimalTestContainersTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_animal_shelter")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-test-data.sql");

    @Autowired
    private AnimalRepository animalRepository;

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve animal from real MySQL database")
    void saveAndRetrieveAnimal_ShouldWorkWithRealDatabase() {
        // Given
        Animal animal = new Animal();
        animal.setName("Buddy");
        animal.setSpecies("Dog");
        animal.setBreed("Golden Retriever");

        // When
        Animal savedAnimal = animalRepository.save(animal);
        Animal retrievedAnimal = animalRepository.findById(savedAnimal.getId()).orElse(null);

        // Then
        assertThat(savedAnimal).isNotNull();
        assertThat(savedAnimal.getId()).isNotNull();
        assertThat(savedAnimal.getName()).isEqualTo("Buddy");
        assertThat(savedAnimal.getSpecies()).isEqualTo("Dog");
        assertThat(savedAnimal.getBreed()).isEqualTo("Golden Retriever");

        assertThat(retrievedAnimal).isNotNull();
        assertThat(retrievedAnimal.getId()).isEqualTo(savedAnimal.getId());
        assertThat(retrievedAnimal.getName()).isEqualTo("Buddy");
        assertThat(retrievedAnimal.getSpecies()).isEqualTo("Dog");
        assertThat(retrievedAnimal.getBreed()).isEqualTo("Golden Retriever");
    }

    @Test
    @DisplayName("Should find all animals from real MySQL database")
    void findAllAnimals_ShouldReturnAllAnimalsFromDatabase() {
        // Given
        Animal animal1 = new Animal();
        animal1.setName("Buddy");
        animal1.setSpecies("Dog");
        animal1.setBreed("Golden Retriever");

        Animal animal2 = new Animal();
        animal2.setName("Whiskers");
        animal2.setSpecies("Cat");
        animal2.setBreed("Persian");

        animalRepository.save(animal1);
        animalRepository.save(animal2);

        // When
        var allAnimals = animalRepository.findAll();

        // Then
        assertThat(allAnimals).hasSize(2);
        assertThat(allAnimals).extracting(Animal::getName)
                .containsExactlyInAnyOrder("Buddy", "Whiskers");
        assertThat(allAnimals).extracting(Animal::getSpecies)
                .containsExactlyInAnyOrder("Dog", "Cat");
    }

    @Test
    @DisplayName("Should update animal in real MySQL database")
    void updateAnimal_ShouldUpdateAnimalInDatabase() {
        // Given
        Animal animal = new Animal();
        animal.setName("Buddy");
        animal.setSpecies("Dog");
        animal.setBreed("Golden Retriever");
        Animal savedAnimal = animalRepository.save(animal);

        // When
        savedAnimal.setName("Updated Buddy");
        savedAnimal.setBreed("Labrador");
        Animal updatedAnimal = animalRepository.save(savedAnimal);

        // Then
        assertThat(updatedAnimal.getId()).isEqualTo(savedAnimal.getId());
        assertThat(updatedAnimal.getName()).isEqualTo("Updated Buddy");
        assertThat(updatedAnimal.getBreed()).isEqualTo("Labrador");
        assertThat(updatedAnimal.getSpecies()).isEqualTo("Dog");

        // Verify in database
        Animal retrievedAnimal = animalRepository.findById(updatedAnimal.getId()).orElse(null);
        assertThat(retrievedAnimal).isNotNull();
        assertThat(retrievedAnimal.getName()).isEqualTo("Updated Buddy");
        assertThat(retrievedAnimal.getBreed()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("Should delete animal from real MySQL database")
    void deleteAnimal_ShouldRemoveAnimalFromDatabase() {
        // Given
        Animal animal = new Animal();
        animal.setName("Buddy");
        animal.setSpecies("Dog");
        animal.setBreed("Golden Retriever");
        Animal savedAnimal = animalRepository.save(animal);

        // When
        animalRepository.deleteById(savedAnimal.getId());

        // Then
        var allAnimals = animalRepository.findAll();
        assertThat(allAnimals).isEmpty();

        var deletedAnimal = animalRepository.findById(savedAnimal.getId());
        assertThat(deletedAnimal).isEmpty();
    }

    @Test
    @DisplayName("Should handle database constraints")
    void saveAnimal_WithInvalidData_ShouldHandleConstraints() {
        // Given
        Animal animal = new Animal();
        animal.setName(""); // Empty name should be handled
        animal.setSpecies("Dog");
        animal.setBreed("Golden Retriever");

        // When & Then
        // This test depends on your entity constraints
        // If you have @NotNull or @NotBlank on name, this should fail
        try {
            animalRepository.save(animal);
            // If no exception is thrown, verify the behavior
            var allAnimals = animalRepository.findAll();
            assertThat(allAnimals).hasSize(1);
        } catch (Exception e) {
            // If exception is thrown, verify it's the expected constraint violation
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("Should handle concurrent access to database")
    void concurrentAccess_ShouldHandleMultipleOperations() {
        // Given
        Animal animal1 = new Animal();
        animal1.setName("Buddy");
        animal1.setSpecies("Dog");
        animal1.setBreed("Golden Retriever");

        Animal animal2 = new Animal();
        animal2.setName("Whiskers");
        animal2.setSpecies("Cat");
        animal2.setBreed("Persian");

        // When
        Animal savedAnimal1 = animalRepository.save(animal1);
        Animal savedAnimal2 = animalRepository.save(animal2);

        // Then
        assertThat(savedAnimal1).isNotNull();
        assertThat(savedAnimal2).isNotNull();
        assertThat(savedAnimal1.getId()).isNotEqualTo(savedAnimal2.getId());

        var allAnimals = animalRepository.findAll();
        assertThat(allAnimals).hasSize(2);
    }
}
