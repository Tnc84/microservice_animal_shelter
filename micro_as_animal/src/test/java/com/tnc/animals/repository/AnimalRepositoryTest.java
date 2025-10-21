package com.tnc.animals.repository;

import com.tnc.animals.repository.entities.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simplified unit tests for Animal entity
 * Tests basic entity functionality without requiring database context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Animal Entity Tests")
class AnimalRepositoryTest {

    private Animal testAnimal1;
    private Animal testAnimal2;
    private Animal testAnimal3;

    @BeforeEach
    void setUp() {
        // Create test animals
        testAnimal1 = new Animal();
        testAnimal1.setName("Buddy");
        testAnimal1.setBreed("Golden Retriever");
        testAnimal1.setSpecies("Dog");
        testAnimal1.setPhoto("buddy.jpg");

        testAnimal2 = new Animal();
        testAnimal2.setName("Whiskers");
        testAnimal2.setBreed("Persian");
        testAnimal2.setSpecies("Cat");
        testAnimal2.setPhoto("whiskers.jpg");

        testAnimal3 = new Animal();
        testAnimal3.setName("Tweety");
        testAnimal3.setBreed("Canary");
        testAnimal3.setSpecies("Bird");
        testAnimal3.setPhoto("tweety.jpg");
    }

    @Test
    @DisplayName("Animal entity should have correct properties")
    void animalEntity_ShouldHaveCorrectProperties() {
        // Act & Assert
        assertThat(testAnimal1).isNotNull();
        assertThat(testAnimal1.getName()).isEqualTo("Buddy");
        assertThat(testAnimal1.getBreed()).isEqualTo("Golden Retriever");
        assertThat(testAnimal1.getSpecies()).isEqualTo("Dog");
        assertThat(testAnimal1.getPhoto()).isEqualTo("buddy.jpg");
    }

    @Test
    @DisplayName("Animal entity should allow property updates")
    void animalEntity_ShouldAllowPropertyUpdates() {
        // Act
        testAnimal1.setName("Updated Buddy");
        testAnimal1.setBreed("Labrador");
        testAnimal1.setSpecies("Dog");
        testAnimal1.setPhoto("updated-buddy.jpg");

        // Assert
        assertThat(testAnimal1.getName()).isEqualTo("Updated Buddy");
        assertThat(testAnimal1.getBreed()).isEqualTo("Labrador");
        assertThat(testAnimal1.getSpecies()).isEqualTo("Dog");
        assertThat(testAnimal1.getPhoto()).isEqualTo("updated-buddy.jpg");
    }

    @Test
    @DisplayName("Multiple animals should have different properties")
    void multipleAnimals_ShouldHaveDifferentProperties() {
        // Assert
        assertThat(testAnimal1.getName()).isEqualTo("Buddy");
        assertThat(testAnimal2.getName()).isEqualTo("Whiskers");
        assertThat(testAnimal3.getName()).isEqualTo("Tweety");
        
        assertThat(testAnimal1.getSpecies()).isEqualTo("Dog");
        assertThat(testAnimal2.getSpecies()).isEqualTo("Cat");
        assertThat(testAnimal3.getSpecies()).isEqualTo("Bird");
    }

    @Test
    @DisplayName("Animal entity should handle null values")
    void animalEntity_ShouldHandleNullValues() {
        // Act
        Animal nullAnimal = new Animal();
        nullAnimal.setName(null);
        nullAnimal.setBreed(null);
        nullAnimal.setSpecies(null);
        nullAnimal.setPhoto(null);

        // Assert
        assertThat(nullAnimal.getName()).isNull();
        assertThat(nullAnimal.getBreed()).isNull();
        assertThat(nullAnimal.getSpecies()).isNull();
        assertThat(nullAnimal.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Animal entity should support different species")
    void animalEntity_ShouldSupportDifferentSpecies() {
        // Assert
        assertThat(testAnimal1.getSpecies()).isEqualTo("Dog");
        assertThat(testAnimal2.getSpecies()).isEqualTo("Cat");
        assertThat(testAnimal3.getSpecies()).isEqualTo("Bird");
    }

    @Test
    @DisplayName("Animal entity should support different breeds")
    void animalEntity_ShouldSupportDifferentBreeds() {
        // Assert
        assertThat(testAnimal1.getBreed()).isEqualTo("Golden Retriever");
        assertThat(testAnimal2.getBreed()).isEqualTo("Persian");
        assertThat(testAnimal3.getBreed()).isEqualTo("Canary");
    }

    @Test
    @DisplayName("Animal entity should support photo URLs")
    void animalEntity_ShouldSupportPhotoUrls() {
        // Assert
        assertThat(testAnimal1.getPhoto()).isEqualTo("buddy.jpg");
        assertThat(testAnimal2.getPhoto()).isEqualTo("whiskers.jpg");
        assertThat(testAnimal3.getPhoto()).isEqualTo("tweety.jpg");
    }

    @Test
    @DisplayName("Animal entity should be mutable")
    void animalEntity_ShouldBeMutable() {
        // Act
        testAnimal1.setName("New Name");
        testAnimal1.setBreed("New Breed");
        testAnimal1.setSpecies("New Species");
        testAnimal1.setPhoto("new-photo.jpg");

        // Assert
        assertThat(testAnimal1.getName()).isEqualTo("New Name");
        assertThat(testAnimal1.getBreed()).isEqualTo("New Breed");
        assertThat(testAnimal1.getSpecies()).isEqualTo("New Species");
        assertThat(testAnimal1.getPhoto()).isEqualTo("new-photo.jpg");
    }

    @Test
    @DisplayName("Animal entity should support empty strings")
    void animalEntity_ShouldSupportEmptyStrings() {
        // Act
        testAnimal1.setName("");
        testAnimal1.setBreed("");
        testAnimal1.setSpecies("");
        testAnimal1.setPhoto("");

        // Assert
        assertThat(testAnimal1.getName()).isEmpty();
        assertThat(testAnimal1.getBreed()).isEmpty();
        assertThat(testAnimal1.getSpecies()).isEmpty();
        assertThat(testAnimal1.getPhoto()).isEmpty();
    }

    @Test
    @DisplayName("Animal entity should support long strings")
    void animalEntity_ShouldSupportLongStrings() {
        // Act
        String longName = "A".repeat(100);
        String longBreed = "B".repeat(100);
        String longSpecies = "C".repeat(100);
        String longPhoto = "D".repeat(100);
        
        testAnimal1.setName(longName);
        testAnimal1.setBreed(longBreed);
        testAnimal1.setSpecies(longSpecies);
        testAnimal1.setPhoto(longPhoto);

        // Assert
        assertThat(testAnimal1.getName()).isEqualTo(longName);
        assertThat(testAnimal1.getBreed()).isEqualTo(longBreed);
        assertThat(testAnimal1.getSpecies()).isEqualTo(longSpecies);
        assertThat(testAnimal1.getPhoto()).isEqualTo(longPhoto);
    }
}