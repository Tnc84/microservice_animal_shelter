package com.tnc.animals.service.impl;

import com.tnc.animals.events.AnimalEventDTO;
import com.tnc.animals.events.AnimalEventPublisher;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.animals.service.interfaces.AnimalService;
import com.tnc.animals.service.mapper.AnimalDomainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalDomainMapper animalDomainMapper;
    private final AnimalEventPublisher animalEventPublisher;

    @Override
    public AnimalDomain get(Long id) {
        return animalDomainMapper.toDomain(animalRepository.findById(id).orElse(null));
    }

    @Override
    public List<AnimalDomain> getAll() {
        return animalDomainMapper.toDomainList(animalRepository.findAll());
    }

    @Override
    @Transactional
    public AnimalDomain add(AnimalDomain animalDomain) {
        log.info("Creating new animal: {}", animalDomain.getName());
        AnimalDomain savedAnimal = animalDomainMapper.toDomain(animalRepository.save(animalDomainMapper.toEntity(animalDomain)));
        
        // Publish animal created event after successful DB commit
        publishAnimalCreatedEvent(savedAnimal);
        
        return savedAnimal;
    }

    @Override
    @Transactional
    public AnimalDomain update(AnimalDomain animalDomain) {
        log.info("Updating animal: {}", animalDomain.getName());
        AnimalDomain updatedAnimal = animalDomainMapper.toDomain(animalRepository.save(animalDomainMapper.toEntity(animalDomain)));
        
        // Publish animal updated event after successful DB commit
        publishAnimalUpdatedEvent(updatedAnimal);
        
        return updatedAnimal;
    }
    
    /**
     * Delete an animal by ID.
     * 
     * @param id the animal ID
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting animal with ID: {}", id);
        AnimalDomain animalToDelete = get(id);
        if (animalToDelete != null) {
            animalRepository.deleteById(id);
            // Publish animal deleted event after successful DB commit
            publishAnimalDeletedEvent(animalToDelete);
        }
    }
    
    /**
     * Mark an animal as adopted.
     * 
     * @param id the animal ID
     * @param userId the user ID who adopted the animal
     * @param userEmail the email of the user who adopted the animal
     */
    @Transactional
    public AnimalDomain adoptAnimal(Long id, Long userId, String userEmail) {
        log.info("Adopting animal with ID: {} by user: {}", id, userId);
        AnimalDomain animal = get(id);
        if (animal != null) {
            // Update animal status to adopted (you might need to add status field to entity)
            AnimalDomain adoptedAnimal = animalDomainMapper.toDomain(animalRepository.save(animalDomainMapper.toEntity(animal)));
            
            // Publish animal adopted event after successful DB commit
            publishAnimalAdoptedEvent(adoptedAnimal, userId, userEmail);
            
            return adoptedAnimal;
        }
        return null;
    }
    
    /**
     * Publish animal created event.
     */
    private void publishAnimalCreatedEvent(AnimalDomain animal) {
        try {
            AnimalEventDTO eventDTO = AnimalEventDTO.builder()
                    .animalId(animal.getId())
                    .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                    .animalName(animal.getName())
                    .animalType(animal.getSpecies())
                    .animalBreed(animal.getBreed())
                    .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                    .timestamp(LocalDateTime.now())
                    .description("New animal added to shelter")
                    .build();
            
            animalEventPublisher.publishAnimalCreated(eventDTO);
        } catch (Exception e) {
            log.error("Failed to publish animal created event for animal ID: {}", animal.getId(), e);
        }
    }
    
    /**
     * Publish animal updated event.
     */
    private void publishAnimalUpdatedEvent(AnimalDomain animal) {
        try {
            AnimalEventDTO eventDTO = AnimalEventDTO.builder()
                    .animalId(animal.getId())
                    .eventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED)
                    .animalName(animal.getName())
                    .animalType(animal.getSpecies())
                    .animalBreed(animal.getBreed())
                    .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                    .timestamp(LocalDateTime.now())
                    .description("Animal information updated")
                    .build();
            
            animalEventPublisher.publishAnimalUpdated(eventDTO);
        } catch (Exception e) {
            log.error("Failed to publish animal updated event for animal ID: {}", animal.getId(), e);
        }
    }
    
    /**
     * Publish animal deleted event.
     */
    private void publishAnimalDeletedEvent(AnimalDomain animal) {
        try {
            AnimalEventDTO eventDTO = AnimalEventDTO.builder()
                    .animalId(animal.getId())
                    .eventType(AnimalEventDTO.EVENT_ANIMAL_DELETED)
                    .animalName(animal.getName())
                    .animalType(animal.getSpecies())
                    .animalBreed(animal.getBreed())
                    .timestamp(LocalDateTime.now())
                    .description("Animal removed from shelter")
                    .build();
            
            animalEventPublisher.publishAnimalDeleted(eventDTO);
        } catch (Exception e) {
            log.error("Failed to publish animal deleted event for animal ID: {}", animal.getId(), e);
        }
    }
    
    /**
     * Publish animal adopted event.
     */
    private void publishAnimalAdoptedEvent(AnimalDomain animal, Long userId, String userEmail) {
        try {
            AnimalEventDTO eventDTO = AnimalEventDTO.builder()
                    .animalId(animal.getId())
                    .eventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED)
                    .animalName(animal.getName())
                    .animalType(animal.getSpecies())
                    .animalBreed(animal.getBreed())
                    .animalStatus(AnimalEventDTO.STATUS_ADOPTED)
                    .userId(userId)
                    .userEmail(userEmail)
                    .timestamp(LocalDateTime.now())
                    .description("Animal has been adopted")
                    .build();
            
            animalEventPublisher.publishAnimalAdopted(eventDTO);
        } catch (Exception e) {
            log.error("Failed to publish animal adopted event for animal ID: {}", animal.getId(), e);
        }
    }

}
