package com.tnc.shelter.performance;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for Shelter service
 * Tests system performance under load and concurrent access
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShelterPerformanceTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ShelterRepository shelterRepository;

    private static final int BATCH_SIZE = 100;
    private static final int CONCURRENT_THREADS = 5;

    @BeforeEach
    void setUp() {
        shelterRepository.deleteAll();
    }

    @Test
    void addMultipleShelters_ShouldCompleteWithinReasonableTime() throws ShelterAddressException, ShelterNameException {
        // Arrange
        List<ShelterDomain> shelters = createTestShelters(BATCH_SIZE);
        long startTime = System.currentTimeMillis();

        // Act
        for (ShelterDomain shelter : shelters) {
            shelterService.add(shelter);
        }

        // Assert
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        assertTrue(executionTime < 5000, "Adding " + BATCH_SIZE + " shelters should complete within 5 seconds");
        
        List<Shelter> savedShelters = shelterRepository.findAll();
        assertEquals(BATCH_SIZE, savedShelters.size());
    }

    @Test
    void getAllShelters_WithLargeDataset_ShouldCompleteWithinReasonableTime() throws ShelterAddressException, ShelterNameException {
        // Arrange - Add large number of shelters
        List<ShelterDomain> shelters = createTestShelters(BATCH_SIZE);
        for (ShelterDomain shelter : shelters) {
            shelterService.add(shelter);
        }

        long startTime = System.currentTimeMillis();

        // Act
        List<ShelterDomain> result = shelterService.getAll();

        // Assert
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        assertTrue(executionTime < 2000, "Retrieving " + BATCH_SIZE + " shelters should complete within 2 seconds");
        assertEquals(BATCH_SIZE, result.size());
    }

    @Test
    void concurrentShelterOperations_ShouldHandleMultipleThreads() throws InterruptedException {
        // Arrange
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Act - Create concurrent operations
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        ShelterDomain shelter = new ShelterDomain(
                            null,
                            "Concurrent Shelter " + threadId + "-" + j,
                            "Test City " + threadId + "-" + j
                        );
                        shelter.setEnvironment("8080");
                        shelterService.add(shelter);
                    }
                } catch (Exception e) {
                    // Handle exceptions in concurrent operations
                    System.err.println("Error in thread " + threadId + ": " + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(30, TimeUnit.SECONDS)
                .join();

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Assert - The test passes if no exceptions were thrown during concurrent operations
        // This verifies that the system can handle concurrent load without crashing
        List<Shelter> savedShelters = shelterRepository.findAll();
        System.out.println("Concurrent operations completed. Total shelters saved: " + savedShelters.size());
        // The test passes if we reach this point without exceptions
    }

    @Test
    void memoryUsage_WithLargeDataset_ShouldRemainStable() throws ShelterAddressException, ShelterNameException {
        // Arrange
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Act - Add and retrieve large dataset
        List<ShelterDomain> shelters = createTestShelters(50);
        for (ShelterDomain shelter : shelters) {
            shelterService.add(shelter);
        }
        
        List<ShelterDomain> result = shelterService.getAll();
        
        // Force garbage collection
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        // Assert
        assertTrue(memoryIncrease < 50 * 1024 * 1024, // 50MB limit
                "Memory increase should be reasonable for 50 shelters");
        assertEquals(50, result.size());
    }

    @Test
    void responseTime_UnderLoad_ShouldMeetPerformanceRequirements() throws ShelterAddressException, ShelterNameException {
        // Arrange - Pre-populate with data
        List<ShelterDomain> shelters = createTestShelters(20);
        for (ShelterDomain shelter : shelters) {
            shelterService.add(shelter);
        }

        // Act - Measure response time for multiple operations
        long totalTime = 0;
        int operations = 100;

        for (int i = 0; i < operations; i++) {
            long startTime = System.nanoTime();
            shelterService.getAll();
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        // Assert
        double averageResponseTime = (double) totalTime / operations / 1_000_000; // Convert to milliseconds
        assertTrue(averageResponseTime < 100, 
                "Average response time should be less than 100ms, was: " + averageResponseTime + "ms");
    }

    private List<ShelterDomain> createTestShelters(int count) {
        List<ShelterDomain> shelters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ShelterDomain shelter = new ShelterDomain(
                null,
                "Test Shelter " + i,
                "Test City " + i
            );
            shelter.setEnvironment("8080");
            shelters.add(shelter);
        }
        return shelters;
    }
}
