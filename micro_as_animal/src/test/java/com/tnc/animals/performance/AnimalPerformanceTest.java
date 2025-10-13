package com.tnc.animals.performance;

import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Performance tests for Animal microservice
 * Tests response times, throughput, and concurrent access
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Animal Performance Tests")
class AnimalPerformanceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnimalRepository animalRepository;

    private MockMvc mockMvc;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        executorService = Executors.newFixedThreadPool(10);
        animalRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /animals/getAll - Should handle 100 concurrent requests")
    void getAllAnimals_With100ConcurrentRequests_ShouldRespondWithinTimeLimit() throws Exception {
        // Given
        createTestAnimals(50); // Create 50 test animals
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // When - Send 100 concurrent requests
        for (int i = 0; i < 100; i++) {
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    mockMvc.perform(get("/animals/getAll"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$").isArray());
                    return System.currentTimeMillis() - requestStart;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long totalTime = System.currentTimeMillis() - startTime;

        // Then
        List<Long> responseTimes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Performance assertions
        assertThat(totalTime).isLessThan(5000); // Total time should be less than 5 seconds
        assertThat(responseTimes.stream().mapToLong(Long::longValue).average().orElse(0))
                .isLessThan(200); // Average response time should be less than 200ms
        assertThat(responseTimes.stream().mapToLong(Long::longValue).max().orElse(0))
                .isLessThan(1000); // Max response time should be less than 1 second

        System.out.println("Performance Test Results:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average response time: " + 
                responseTimes.stream().mapToLong(Long::longValue).average().orElse(0) + "ms");
        System.out.println("Max response time: " + 
                responseTimes.stream().mapToLong(Long::longValue).max().orElse(0) + "ms");
    }

    @Test
    @DisplayName("POST /animals - Should handle 50 concurrent creates")
    void createAnimal_With50ConcurrentCreates_ShouldHandleGracefully() throws Exception {
        // Given
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // When - Send 50 concurrent create requests
        for (int i = 0; i < 50; i++) {
            final int index = i;
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    AnimalDTO animalDTO = createAnimalDTO("Animal " + index, "Dog", "Breed " + index);
                    mockMvc.perform(post("/animals")
                            .contentType("application/json")
                            .content(createJsonContent(animalDTO)))
                            .andExpect(status().isOk());
                    return true;
                } catch (Exception e) {
                    System.err.println("Error in concurrent create: " + e.getMessage());
                    return false;
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long totalTime = System.currentTimeMillis() - startTime;

        // Then
        long successCount = futures.stream().mapToLong(f -> f.join() ? 1 : 0).sum();
        assertThat(successCount).isGreaterThan(40); // At least 80% should succeed
        assertThat(totalTime).isLessThan(10000); // Total time should be less than 10 seconds

        // Verify animals were created
        List<Animal> allAnimals = animalRepository.findAll();
        assertThat(allAnimals).hasSize((int) successCount);

        System.out.println("Concurrent Create Test Results:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Successful creates: " + successCount + "/50");
        System.out.println("Success rate: " + (successCount * 100.0 / 50) + "%");
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should handle 200 concurrent reads")
    void getAnimalById_With200ConcurrentReads_ShouldRespondWithinTimeLimit() throws Exception {
        // Given
        List<Animal> testAnimals = createTestAnimals(20);
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // When - Send 200 concurrent read requests
        for (int i = 0; i < 200; i++) {
            final Animal testAnimal = testAnimals.get(i % testAnimals.size());
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    mockMvc.perform(get("/animals/getById/{id}", testAnimal.getId()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(testAnimal.getId()));
                    return System.currentTimeMillis() - requestStart;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long totalTime = System.currentTimeMillis() - startTime;

        // Then
        List<Long> responseTimes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Performance assertions
        assertThat(totalTime).isLessThan(3000); // Total time should be less than 3 seconds
        assertThat(responseTimes.stream().mapToLong(Long::longValue).average().orElse(0))
                .isLessThan(100); // Average response time should be less than 100ms

        System.out.println("Concurrent Read Test Results:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average response time: " + 
                responseTimes.stream().mapToLong(Long::longValue).average().orElse(0) + "ms");
        System.out.println("Max response time: " + 
                responseTimes.stream().mapToLong(Long::longValue).max().orElse(0) + "ms");
    }

    @Test
    @DisplayName("PUT /animals - Should handle 30 concurrent updates")
    void updateAnimal_With30ConcurrentUpdates_ShouldHandleGracefully() throws Exception {
        // Given
        List<Animal> testAnimals = createTestAnimals(30);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // When - Send 30 concurrent update requests
        for (int i = 0; i < 30; i++) {
            final Animal testAnimal = testAnimals.get(i);
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    AnimalDTO animalDTO = createAnimalDTO(testAnimal.getId(), 
                            "Updated " + testAnimal.getName(), 
                            testAnimal.getSpecies(), 
                            "Updated " + testAnimal.getBreed());
                    mockMvc.perform(put("/animals")
                            .contentType("application/json")
                            .content(createJsonContent(animalDTO)))
                            .andExpect(status().isOk());
                    return true;
                } catch (Exception e) {
                    System.err.println("Error in concurrent update: " + e.getMessage());
                    return false;
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long totalTime = System.currentTimeMillis() - startTime;

        // Then
        long successCount = futures.stream().mapToLong(f -> f.join() ? 1 : 0).sum();
        assertThat(successCount).isGreaterThan(25); // At least 83% should succeed
        assertThat(totalTime).isLessThan(5000); // Total time should be less than 5 seconds

        System.out.println("Concurrent Update Test Results:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Successful updates: " + successCount + "/30");
        System.out.println("Success rate: " + (successCount * 100.0 / 30) + "%");
    }

    @Test
    @DisplayName("Mixed operations - Should handle 100 mixed requests")
    void mixedOperations_With100MixedRequests_ShouldHandleGracefully() throws Exception {
        // Given
        List<Animal> testAnimals = createTestAnimals(10);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // When - Send 100 mixed requests (40% read, 30% create, 30% update)
        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    if (index % 10 < 4) { // 40% read operations
                        Animal testAnimal = testAnimals.get(index % testAnimals.size());
                        mockMvc.perform(get("/animals/getById/{id}", testAnimal.getId()))
                                .andExpect(status().isOk());
                    } else if (index % 10 < 7) { // 30% create operations
                        AnimalDTO animalDTO = createAnimalDTO("Mixed Animal " + index, "Dog", "Breed " + index);
                        mockMvc.perform(post("/animals")
                                .contentType("application/json")
                                .content(createJsonContent(animalDTO)))
                                .andExpect(status().isOk());
                    } else { // 30% update operations
                        Animal testAnimal = testAnimals.get(index % testAnimals.size());
                        AnimalDTO animalDTO = createAnimalDTO(testAnimal.getId(), 
                                "Updated Mixed " + testAnimal.getName(), 
                                testAnimal.getSpecies(), 
                                testAnimal.getBreed());
                        mockMvc.perform(put("/animals")
                                .contentType("application/json")
                                .content(createJsonContent(animalDTO)))
                                .andExpect(status().isOk());
                    }
                    return true;
                } catch (Exception e) {
                    System.err.println("Error in mixed operation: " + e.getMessage());
                    return false;
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long totalTime = System.currentTimeMillis() - startTime;

        // Then
        long successCount = futures.stream().mapToLong(f -> f.join() ? 1 : 0).sum();
        assertThat(successCount).isGreaterThan(80); // At least 80% should succeed
        assertThat(totalTime).isLessThan(10000); // Total time should be less than 10 seconds

        System.out.println("Mixed Operations Test Results:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Successful operations: " + successCount + "/100");
        System.out.println("Success rate: " + (successCount * 100.0 / 100) + "%");
    }

    // Helper methods
    private List<Animal> createTestAnimals(int count) {
        List<Animal> animals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Animal animal = new Animal();
            animal.setName("Test Animal " + i);
            animal.setSpecies("Dog");
            animal.setBreed("Breed " + i);
            animals.add(animalRepository.save(animal));
        }
        return animals;
    }

    private AnimalDTO createAnimalDTO(Long id, String name, String species, String breed) {
        return new AnimalDTO(id, name, breed, species, null);
    }

    private AnimalDTO createAnimalDTO(String name, String species, String breed) {
        return createAnimalDTO(null, name, species, breed);
    }

    private String createJsonContent(AnimalDTO animalDTO) {
        return String.format("""
                {
                    "id": %s,
                    "name": "%s",
                    "species": "%s",
                    "breed": "%s"
                }
                """, 
                animalDTO.id() != null ? animalDTO.id() : "null",
                animalDTO.name(),
                animalDTO.species(),
                animalDTO.breed());
    }
}
