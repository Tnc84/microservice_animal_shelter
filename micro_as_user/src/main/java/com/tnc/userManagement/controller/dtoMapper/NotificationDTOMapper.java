package com.tnc.userManagement.controller.dtoMapper;

import com.tnc.userManagement.controller.dto.NotificationDTO;
import com.tnc.userManagement.repository.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between Notification entity and NotificationDTO.
 * Handles mapping between database entities and REST API DTOs.
 */
@Mapper(componentModel = "spring")
public interface NotificationDTOMapper {
    
    /**
     * Convert Notification entity to NotificationDTO.
     * 
     * @param notification the notification entity
     * @return the notification DTO
     */
    NotificationDTO toDTO(Notification notification);
    
    /**
     * Convert NotificationDTO to Notification entity.
     * 
     * @param notificationDTO the notification DTO
     * @return the notification entity
     */
    Notification toEntity(NotificationDTO notificationDTO);
    
    /**
     * Convert list of Notification entities to list of NotificationDTOs.
     * 
     * @param notifications the list of notification entities
     * @return the list of notification DTOs
     */
    List<NotificationDTO> toDTOList(List<Notification> notifications);
    
    /**
     * Convert list of NotificationDTOs to list of Notification entities.
     * 
     * @param notificationDTOs the list of notification DTOs
     * @return the list of notification entities
     */
    List<Notification> toEntityList(List<NotificationDTO> notificationDTOs);
}
