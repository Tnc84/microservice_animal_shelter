package com.tnc.userManagement.controller;

import com.tnc.userManagement.controller.dto.NotificationDTO;
import com.tnc.userManagement.controller.dtoMapper.NotificationDTOMapper;
import com.tnc.userManagement.repository.entity.Notification;
import com.tnc.userManagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user notifications.
 * Provides endpoints for retrieving, marking as read, and managing notifications.
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final NotificationDTOMapper notificationDTOMapper;
    
    /**
     * Get paginated notifications for a user.
     * 
     * @param userId the user ID
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @return page of notifications
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieve paginated notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<NotificationDTO>> getUserNotifications(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Retrieving notifications for user ID: {}, page: {}, size: {}", userId, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        Page<NotificationDTO> notificationDTOs = notifications.map(notificationDTOMapper::toDTO);
        return ResponseEntity.ok(notificationDTOs);
    }
    
    /**
     * Get unread notifications for a user.
     * 
     * @param userId the user ID
     * @return list of unread notifications
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieve all unread notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        log.info("Retrieving unread notifications for user ID: {}", userId);
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationDTO> notificationDTOs = notificationDTOMapper.toDTOList(notifications);
        return ResponseEntity.ok(notificationDTOs);
    }
    
    /**
     * Get notification count for a user.
     * 
     * @param userId the user ID
     * @return notification count
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get notification count", description = "Get the count of unread notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification count retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getNotificationCount(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        log.info("Retrieving notification count for user ID: {}", userId);
        long count = notificationService.getNotificationCount(userId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Get notifications by type for a user.
     * 
     * @param userId the user ID
     * @param type the notification type
     * @param page page number (0-based)
     * @param size page size
     * @return page of notifications
     */
    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieve paginated notifications of a specific type for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<NotificationDTO>> getNotificationsByType(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Notification type", required = true, example = "ANIMAL_CREATED")
            @PathVariable String type,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Retrieving notifications for user ID: {}, type: {}, page: {}", userId, type, page);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationService.getNotificationsByType(userId, type, pageable);
        Page<NotificationDTO> notificationDTOs = notifications.map(notificationDTOMapper::toDTO);
        return ResponseEntity.ok(notificationDTOs);
    }
    
    /**
     * Mark a notification as read.
     * 
     * @param notificationId the notification ID
     * @param userId the user ID (for security)
     * @return success response
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found or not owned by user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "Notification ID", required = true, example = "1")
            @PathVariable Long notificationId,
            @Parameter(description = "User ID", required = true, example = "1")
            @RequestParam Long userId) {
        
        log.info("Marking notification ID: {} as read for user ID: {}", notificationId, userId);
        
        boolean success = notificationService.markAsRead(notificationId, userId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mark all notifications as read for a user.
     * 
     * @param userId the user ID
     * @return number of notifications marked as read
     */
    @PutMapping("/user/{userId}/mark-all-read")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All notifications marked as read successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Integer> markAllAsRead(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        log.info("Marking all notifications as read for user ID: {}", userId);
        int updatedCount = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(updatedCount);
    }
    
    /**
     * Delete a notification.
     * 
     * @param notificationId the notification ID
     * @param userId the user ID (for security)
     * @return success response
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found or not owned by user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "Notification ID", required = true, example = "1")
            @PathVariable Long notificationId,
            @Parameter(description = "User ID", required = true, example = "1")
            @RequestParam Long userId) {
        
        log.info("Deleting notification ID: {} for user ID: {}", notificationId, userId);
        
        boolean success = notificationService.deleteNotification(notificationId, userId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
