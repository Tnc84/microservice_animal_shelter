package com.tnc.animals.controller;

import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.controller.mapper.AnimalDTOMapper;
import com.tnc.animals.service.interfaces.AnimalService;
import com.tnc.animals.service.validation.OnCreate;
import com.tnc.animals.service.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/animals")
@Validated
@Tag(name = "Animal Management", description = "APIs for managing animal records in the shelter system")
@PreAuthorize("hasAnyRole('ADMIN', 'SHELTER_MANAGER', 'VET')")
public record AnimalController (
AnimalService animalService,
AnimalDTOMapper animalDTOMapper){

    @GetMapping
    @RequestMapping("/getById/{id}")
    @Operation(summary = "Get animal by ID", description = "Retrieve a specific animal by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnimalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Animal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AnimalDTO> get(
            @Parameter(description = "Animal ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(animalDTOMapper.toDTO(animalService.get(id)));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all animals", description = "Retrieve a list of all animals in the shelter system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animals retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnimalDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AnimalDTO>> getAll() {
        return ResponseEntity.ok(animalDTOMapper.toDTOList(animalService.getAll()));
    }

    @PostMapping
    @Validated(OnCreate.class)
    @Operation(summary = "Add new animal", description = "Create a new animal record in the shelter system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnimalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AnimalDTO> add(
            @Parameter(description = "Animal data", required = true)
            @Valid @RequestBody AnimalDTO animalDTO) {
        var addOne = animalService.add(animalDTOMapper.toDomain(animalDTO));
//        var getOne = animalService.add(animalDTOMapper.toDomain(animalDTO));
        return ResponseEntity.ok(animalDTOMapper.toDTO(addOne));
    }

    @PutMapping
    @Validated(OnUpdate.class)
    @Operation(summary = "Update animal", description = "Update an existing animal record in the shelter system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnimalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Animal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AnimalDTO> update(
            @Parameter(description = "Updated animal data", required = true)
            @Valid @RequestBody AnimalDTO animalDTO) {
        var updateAnimal = animalService.update(animalDTOMapper.toDomain(animalDTO));
        return ResponseEntity.ok(animalDTOMapper.toDTO(updateAnimal));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete animal", description = "Remove an animal from the shelter system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Animal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Animal ID to delete", required = true, example = "1")
            @PathVariable Long id) {
        ((com.tnc.animals.service.impl.AnimalServiceImpl) animalService).delete(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/adopt")
    @Operation(summary = "Adopt animal", description = "Mark an animal as adopted by a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal adopted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnimalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Animal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AnimalDTO> adoptAnimal(
            @Parameter(description = "Animal ID to adopt", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "User ID who is adopting", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "User email", required = true, example = "user@example.com")
            @RequestParam String userEmail) {
        var adoptedAnimal = ((com.tnc.animals.service.impl.AnimalServiceImpl) animalService).adoptAnimal(id, userId, userEmail);
        if (adoptedAnimal != null) {
            return ResponseEntity.ok(animalDTOMapper.toDTO(adoptedAnimal));
        }
        return ResponseEntity.notFound().build();
    }
}
