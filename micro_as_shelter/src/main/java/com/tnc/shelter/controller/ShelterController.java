package com.tnc.shelter.controller;

import com.tnc.shelter.controller.dto.AnimalDTO;
import com.tnc.shelter.controller.dto.ShelterDTO;
import com.tnc.shelter.controller.mapper.ShelterDTOMapper;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.impl.FeignAnimalProxy;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.validation.OnCreate;
import com.tnc.shelter.service.validation.OnUpdate;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/shelters")
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Shelter Management", description = "APIs for managing shelter operations and animal integration")
//@PreAuthorize("isAuthenticated() && hasRole('MOD')")
public class ShelterController {

    private final Logger logger = LoggerFactory.getLogger(ShelterController.class);
    private final FeignAnimalProxy feignAnimalProxy;
    private final ShelterService shelterService;
    private final ShelterDTOMapper shelterDTOMapper;

    @GetMapping("/getAllAnimals")
    @Retry(name = "getAllAnimals", fallbackMethod = "fallbackResponse")
    public List<AnimalDTO> getAllAnimalsFeign(){
        logger.info("getAllAnimals method call from shelter-microservice received");
        return feignAnimalProxy.getAllAnimals();
    }

    private List<AnimalDTO> fallbackResponse(Exception e){
        logger.error("Animal server is down");
        return null;
    }

    @GetMapping("/getAnimalByIdFeign/{id}")
    public FeignAnimalProxy getAnimalById() {
        return feignAnimalProxy;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ShelterDTO>> getAll() {
        logger.error("retrieve all shelters");
        return ResponseEntity.ok(shelterDTOMapper.toDTOList(shelterService.getAll()));
    }
    @GetMapping("/iasi")
    public ResponseEntity<ShelterDTO> getShelterByName() {
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.getShelterByName()));
    }

    @PostMapping("/add")
    @Validated(OnCreate.class)
    public ResponseEntity<ShelterDTO> add(@Valid @RequestBody ShelterDTO shelterDTO) throws ShelterAddressException, ShelterNameException {
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.add(shelterDTOMapper.toDomain(shelterDTO))));
    }

    @PutMapping("/update")
    @Validated(OnUpdate.class)
    public ResponseEntity<ShelterDTO> update(@Valid @RequestBody ShelterDTO shelterDTO) throws ShelterAddressException, ShelterNameException {
        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.update(shelterDTOMapper.toDomain(shelterDTO))));
    }

//    @GetMapping(value = "/{id}")
//    public ResponseEntity<ShelterDTO> get(@PathVariable Long id) {
//        return ResponseEntity.ok(shelterDTOMapper.toDTO(shelterService.get(id)));
//    }


}
