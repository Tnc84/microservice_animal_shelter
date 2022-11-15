package com.tnc.shelter.service.impl;

import com.tnc.shelter.controller.dto.AnimalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//@FeignClient(name="Animal-microservice", url = "localhost:8093")
@FeignClient(name="Animal-microservice")
public interface FeignAnimalProxy {

    @GetMapping("animals/getAll")
    List<AnimalDTO> getAllAnimals();

//    @GetMapping("/animals/getById/{id}")
//    AnimalDTO getAnimalById();
}
