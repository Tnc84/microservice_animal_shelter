package com.tnc.shelter.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelterDomain {
    private  Long id;
    private  String name;
    private  String city;
//    private List<AnimalDomain> animals = new ArrayList<>();
    
    // Statistics fields for tracking animal events
    private  Integer animalCount = 0;
    private  Integer maxCapacity = 100; // Default capacity
    private  Integer adoptionCount = 0;
    private java.time.LocalDateTime lastModified;

    public ShelterDomain(Long id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

}
