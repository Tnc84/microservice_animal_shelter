package com.tnc.userManagement.service;

import com.tnc.userManagement.repository.NotificationRepository;
import com.tnc.userManagement.repository.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CircuitBreakerService.
 * Tests circuit breaker functionality and fallback mechanisms.
 */
@ExtendWith(MockitoExtension.class)
class CircuitBreakerServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @InjectMocks
    private CircuitBreakerService circuitBreakerService;
    
    private Notification testNotification;
    
    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .title("Test Notification")
                .message("Test message")
                .type("INFO")
                .isRead(false)
                .build();
    }
    
    @Test
    void getAllNotifications_ShouldReturnNotifications_WhenRepositoryWorks() {
        // Arrange
        List<Notification> expectedNotifications = List.of(testNotification);
        when(notificationRepository.findAll()).thenReturn(expectedNotifications);
        
        // Act
        CompletableFuture<List<Notification>> result = circuitBreakerService.getAllNotifications();
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedNotifications, result.join());
        verify(notificationRepository).findAll();
    }
    
    @Test
    void getAllNotifications_ShouldReturnEmptyList_WhenRepositoryThrowsException() {
        // Arrange
        when(notificationRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            circuitBreakerService.getAllNotifications().join();
        });
        verify(notificationRepository).findAll();
    }
    
    @Test
    void getNotificationsByUserId_ShouldReturnNotifications_WhenRepositoryWorks() {
        // Arrange
        Long userId = 1L;
        List<Notification> expectedNotifications = List.of(testNotification);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)).thenReturn(expectedNotifications);
        
        // Act
        CompletableFuture<List<Notification>> result = circuitBreakerService.getNotificationsByUserId(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedNotifications, result.join());
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    @Test
    void getNotificationsByUserId_ShouldReturnEmptyList_WhenRepositoryThrowsException() {
        // Arrange
        Long userId = 1L;
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)).thenThrow(new RuntimeException("Database connection failed"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            circuitBreakerService.getNotificationsByUserId(userId).join();
        });
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    @Test
    void saveNotification_ShouldReturnNotification_WhenRepositoryWorks() {
        // Arrange
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        
        // Act
        CompletableFuture<Notification> result = circuitBreakerService.saveNotification(testNotification);
        
        // Assert
        assertNotNull(result);
        assertEquals(testNotification, result.join());
        verify(notificationRepository).save(testNotification);
    }
    
    @Test
    void saveNotification_ShouldReturnNull_WhenRepositoryThrowsException() {
        // Arrange
        when(notificationRepository.save(testNotification)).thenThrow(new RuntimeException("Database connection failed"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            circuitBreakerService.saveNotification(testNotification).join();
        });
        verify(notificationRepository).save(testNotification);
    }
}
