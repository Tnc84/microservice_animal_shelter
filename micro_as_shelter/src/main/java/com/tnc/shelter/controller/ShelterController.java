package com.tnc.shelter.controller;

import com.tnc.shelter.controller.dto.ShelterDTO;
import com.tnc.shelter.controller.mapper.ShelterDTOMapper;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.validation.OnCreate;
import com.tnc.shelter.service.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RequestMapping(value = "/shelters")
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Shelter Management", description = "APIs for managing shelter operations and animal integration")
@PreAuthorize("hasAnyRole('ADMIN', 'SHELTER_MANAGER')")
public class ShelterController {

    private final Logger logger = LoggerFactory.getLogger(ShelterController.class);
    private final ShelterService shelterService;
    private final ShelterDTOMapper shelterDTOMapper;


    @GetMapping("/getAll")
    @Operation(summary = "Get all shelters", description = "Retrieve a list of all shelters with statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelters retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShelterDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ShelterDTO>> getAll() {
        logger.info("Retrieving all shelters");
        return ResponseEntity.ok(shelterDTOMapper.toDTOList(shelterService.getAll()));
    }
    
    @GetMapping("/iasi")
    @Operation(summary = "Get shelter by name", description = "Retrieve a specific shelter by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelter found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShelterDTO.class))),
            @ApiResponse(responseCode = "404", description = "Shelter not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ShelterDTO> getShelterByName() {
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.getShelterByName()));
    }

    @PostMapping("/add")
    @Validated(OnCreate.class)
    @Operation(summary = "Add new shelter", description = "Create a new shelter with initial statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelter created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShelterDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ShelterDTO> add(@Valid @RequestBody ShelterDTO shelterDTO) throws ShelterAddressException, ShelterNameException {
        logger.info("Creating new shelter: {}", shelterDTO.getName());
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.add(shelterDTOMapper.toDomain(shelterDTO))));
    }

    @PutMapping("/update")
    @Validated(OnUpdate.class)
    @Operation(summary = "Update shelter", description = "Update an existing shelter's information and statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelter updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShelterDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Shelter not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ShelterDTO> update(@Valid @RequestBody ShelterDTO shelterDTO) throws ShelterAddressException, ShelterNameException {
        logger.info("Updating shelter: {}", shelterDTO.getName());
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.update(shelterDTOMapper.toDomain(shelterDTO))));
    }

//    @GetMapping(value = "/{id}")
//    public ResponseEntity<ShelterDTO> get(@PathVariable Long id) {
//        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.get(id)));
//    }


}
