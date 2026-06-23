package com.tnc.userManagement.service;

import com.tnc.userManagement.repository.NotificationRepository;
import com.tnc.userManagement.repository.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService.
 * Tests the notification management functionality with mocked repository.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @InjectMocks
    private NotificationService notificationService;
    
    private Notification testNotification;
    
    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .title("Test Notification")
                .message("This is a test notification")
                .type(Notification.TYPE_ANIMAL_CREATED)
                .animalId(1L)
                .shelterId(1L)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // When
        Notification result = notificationService.createNotification(
                1L, "Test Title", "Test Message", "TEST_TYPE", 1L, 1L
        );
        
        // Then
        assertNotNull(result);
        assertEquals(testNotification.getId(), result.getId());
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    void getUserNotifications_ShouldReturnPageOfNotifications() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        Page<Notification> page = new PageImpl<>(notifications);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(page);
        
        // When
        Page<Notification> result = notificationService.getUserNotifications(1L, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
    }
    
    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotifications() {
        // Given
        List<Notification> unreadNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(unreadNotifications);
        
        // When
        List<Notification> result = notificationService.getUnreadNotifications(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L);
    }
    
    @Test
    void getNotificationCount_ShouldReturnCount() {
        // Given
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(5L);
        
        // When
        long count = notificationService.getNotificationCount(1L);
        
        // Then
        assertEquals(5L, count);
        verify(notificationRepository).countByUserIdAndIsReadFalse(1L);
    }
    
    @Test
    void markAsRead_WhenNotificationExistsAndOwnedByUser_ShouldReturnTrue() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // When
        boolean result = notificationService.markAsRead(1L, 1L);
        
        // Then
        assertTrue(result);
        assertTrue(testNotification.getIsRead());
        assertNotNull(testNotification.getReadAt());
        verify(notificationRepository).save(testNotification);
    }
    
    @Test
    void markAsRead_WhenNotificationNotOwnedByUser_ShouldReturnFalse() {
        // Given
        testNotification.setUserId(2L); // Different user
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        
        // When
        boolean result = notificationService.markAsRead(1L, 1L);
        
        // Then
        assertFalse(result);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
    
    @Test
    void markAsRead_WhenNotificationNotFound_ShouldReturnFalse() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        boolean result = notificationService.markAsRead(1L, 1L);
        
        // Then
        assertFalse(result);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
    
    @Test
    void markAllAsRead_ShouldUpdateAllNotifications() {
        // Given
        when(notificationRepository.markAllAsReadByUserId(1L)).thenReturn(3);
        
        // When
        int result = notificationService.markAllAsRead(1L);
        
        // Then
        assertEquals(3, result);
        verify(notificationRepository).markAllAsReadByUserId(1L);
    }
    
    @Test
    void deleteNotification_WhenNotificationExistsAndOwnedByUser_ShouldReturnTrue() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        
        // When
        boolean result = notificationService.deleteNotification(1L, 1L);
        
        // Then
        assertTrue(result);
        verify(notificationRepository).deleteById(1L);
    }
    
    @Test
    void deleteNotification_WhenNotificationNotOwnedByUser_ShouldReturnFalse() {
        // Given
        testNotification.setUserId(2L); // Different user
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        
        // When
        boolean result = notificationService.deleteNotification(1L, 1L);
        
        // Then
        assertFalse(result);
        verify(notificationRepository, never()).deleteById(any());
    }
    
    @Test
    void cleanupOldNotifications_ShouldDeleteOldNotifications() {
        // Given
        when(notificationRepository.deleteOldNotifications(any(LocalDateTime.class))).thenReturn(5);
        
        // When
        int result = notificationService.cleanupOldNotifications(30);
        
        // Then
        assertEquals(5, result);
        verify(notificationRepository).deleteOldNotifications(any(LocalDateTime.class));
    }
}
