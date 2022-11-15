package com.tnc.shelter.controller.dto;

public record AnimalDTO(
        Long id,
        String name,
//        String breed,
//        String species,
        String photo,
        String environment
) {
}
