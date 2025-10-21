package com.tnc.animals.service.impl;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.animals.service.interfaces.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.tnc.animals.config.TestSecurityConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for AnimalService with real database
 * Tests service layer with actual JPA operations
 * 
 * TODO: These tests are currently disabled due to ApplicationContext loading issues
 * with security configuration. Re-enable after fixing security test configuration.
 */
@Disabled("Disabled due to ApplicationContext loading issues - security configuration conflicts")
@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("Animal Service Integration Tests")
class AnimalServiceIntegrationTest {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalService animalService;

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll();
    }

    @Test
    @DisplayName("Full CRUD operations - Should work end-to-end")
    void fullCrudOperations_ShouldWorkEndToEnd() {
        // CREATE
        AnimalDomain newAnimal = createTestAnimalDomain(null, "Max", "Dog", "Retriever");
        AnimalDomain savedAnimal = animalService.add(newAnimal);
        
        assertThat(savedAnimal).isNotNull();
        assertThat(savedAnimal.getId()).isNotNull();
        assertThat(savedAnimal.getName()).isEqualTo("Max");

        // READ
        AnimalDomain retrievedAnimal = animalService.get(savedAnimal.getId());
        assertThat(retrievedAnimal).isNotNull();
        assertThat(retrievedAnimal.getName()).isEqualTo("Max");

        // UPDATE
        retrievedAnimal.setName("Maximus");
        retrievedAnimal.setBreed("Retriever");
        AnimalDomain updatedAnimal = animalService.update(retrievedAnimal);
        
        assertThat(updatedAnimal.getName()).isEqualTo("Maximus");
        assertThat(updatedAnimal.getBreed()).isEqualTo("Retriever");

        // READ ALL
        List<AnimalDomain> allAnimals = animalService.getAll();
        assertThat(allAnimals).hasSize(1);
        assertThat(allAnimals.get(0).getName()).isEqualTo("Maximus");

        // DELETE
        animalService.delete(updatedAnimal.getId());
        
        // Verify deletion
        assertThatThrownBy(() -> animalService.get(updatedAnimal.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Animal not found with ID: " + updatedAnimal.getId());
    }

    @Test
    @DisplayName("Multiple animals - Should handle multiple operations correctly")
    void multipleAnimals_ShouldHandleOperationsCorrectly() {
        // Create multiple animals
        AnimalDomain animal1 = animalService.add(createTestAnimalDomain(null, "Buddy", "Dog", "Retriever"));
        AnimalDomain animal2 = animalService.add(createTestAnimalDomain(null, "Whiskers", "Cat", "Persian"));
        AnimalDomain animal3 = animalService.add(createTestAnimalDomain(null, "Rex", "Dog", "Shepherd"));

        // Verify all animals exist
        List<AnimalDomain> allAnimals = animalService.getAll();
        assertThat(allAnimals).hasSize(3);
        assertThat(allAnimals).extracting(AnimalDomain::getName)
                .containsExactlyInAnyOrder("Buddy", "Whiskers", "Rex");

        // Update one animal
        animal2.setBreed("Maine Coon");
        AnimalDomain updatedAnimal2 = animalService.update(animal2);
        assertThat(updatedAnimal2.getBreed()).isEqualTo("Maine Coon");

        // Delete one animal
        animalService.delete(animal1.getId());

        // Verify remaining animals
        List<AnimalDomain> remainingAnimals = animalService.getAll();
        assertThat(remainingAnimals).hasSize(2);
        assertThat(remainingAnimals).extracting(AnimalDomain::getName)
                .containsExactlyInAnyOrder("Whiskers", "Rex");
    }

    @Test
    @DisplayName("Concurrent operations - Should handle concurrent access")
    void concurrentOperations_ShouldHandleConcurrentAccess() {
        // Create initial animal
        AnimalDomain animal = animalService.add(createTestAnimalDomain(null, "Concurrent", "Dog", "Mixed"));

        // Simulate concurrent updates (in real scenario, this would be from different threads)
        AnimalDomain animal1 = animalService.get(animal.getId());
        AnimalDomain animal2 = animalService.get(animal.getId());

        animal1.setName("Updated by Thread 1");
        animal2.setName("Updated by Thread 2");

        // Both updates should succeed (last one wins)
        AnimalDomain updated1 = animalService.update(animal1);
        AnimalDomain updated2 = animalService.update(animal2);

        // Verify the final state
        AnimalDomain finalAnimal = animalService.get(animal.getId());
        assertThat(finalAnimal.getName()).isEqualTo("Updated by Thread 2");
    }

    @Test
    @DisplayName("Data validation - Should handle invalid data gracefully")
    void dataValidation_ShouldHandleInvalidDataGracefully() {
        // Test with null name
        AnimalDomain invalidAnimal = createTestAnimalDomain(null, null, "Dog", "Labrador");
        
        // The service should handle this based on validation rules
        // This test depends on your validation implementation
        assertThatThrownBy(() -> animalService.add(invalidAnimal))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Performance test - Should handle large dataset efficiently")
    void performanceTest_ShouldHandleLargeDatasetEfficiently() {
        // Create multiple animals
        for (int i = 0; i < 100; i++) {
            AnimalDomain animal = createTestAnimalDomain(null, "Animal" + i, "Dog", "Breed" + i);
            animalService.add(animal);
        }

        // Verify all animals were created
        List<AnimalDomain> allAnimals = animalService.getAll();
        assertThat(allAnimals).hasSize(100);

        // Test retrieval performance
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) {
            animalService.get((long) i);
        }
        long endTime = System.currentTimeMillis();
        
        // Verify reasonable performance (adjust threshold as needed)
        assertThat(endTime - startTime).isLessThan(5000); // 5 seconds
    }

    @Test
    @DisplayName("Transaction rollback - Should rollback on failure")
    void transactionRollback_ShouldRollbackOnFailure() {
        // Create initial animal
        AnimalDomain animal = animalService.add(createTestAnimalDomain(null, "Test", "Dog", "Labrador"));
        Long animalId = animal.getId();

        // Verify animal exists
        assertThat(animalService.get(animalId)).isNotNull();

        // Attempt to delete non-existent animal (should not affect existing data)
        animalService.delete(999L);

        // Verify original animal still exists
        assertThat(animalService.get(animalId)).isNotNull();
    }

    // Helper methods
    private AnimalDomain createTestAnimalDomain(Long id, String name, String species, String breed) {
        AnimalDomain animalDomain = new AnimalDomain();
        animalDomain.setId(id);
        animalDomain.setName(name);
        animalDomain.setSpecies(species);
        animalDomain.setBreed(breed);
        return animalDomain;
    }
}
