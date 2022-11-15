package com.tnc.animals.controller;

import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.controller.mapper.AnimalDTOMapper;
import com.tnc.animals.service.interfaces.AnimalService;
import com.tnc.animals.service.validation.OnCreate;
import com.tnc.animals.service.validation.OnUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animals")
@Validated
//@PreAuthorize("isAuthenticated() && hasRole('USER')")
public class AnimalController {

    private final AnimalService animalService;
    private final AnimalDTOMapper animalDTOMapper;

    @GetMapping
    @RequestMapping("/getById/{id}")
    public ResponseEntity<AnimalDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(animalDTOMapper.toDTO(animalService.get(id)));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AnimalDTO>> getAll() {
        return ResponseEntity.ok(animalDTOMapper.toDTOList(animalService.getAll()));
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<AnimalDTO> add(@Valid @RequestBody AnimalDTO animalDTO) {
        var addOne = animalService.add(animalDTOMapper.toDomain(animalDTO));
//        var getOne = animalService.add(animalDTOMapper.toDomain(animalDTO));
        return ResponseEntity.ok(animalDTOMapper.toDTO(addOne));
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<AnimalDTO> update(@Valid @RequestBody AnimalDTO animalDTO) {
        var updateAnimal = animalService.update(animalDTOMapper.toDomain(animalDTO));
        return ResponseEntity.ok(animalDTOMapper.toDTO(updateAnimal));
    }
}
