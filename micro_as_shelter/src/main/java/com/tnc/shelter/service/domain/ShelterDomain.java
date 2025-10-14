package com.tnc.shelter.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
//@NoArgsConstructor
//@Accessors(chain = true)
public class ShelterDomain {
    private final Long id;
    private final String name;
    private final String city;
//    private List<AnimalDomain> animals = new ArrayList<>();
    
    // Statistics fields for tracking animal events
    private Integer animalCount = 0;
    private Integer maxCapacity = 100; // Default capacity
    private Integer adoptionCount = 0;
    private java.time.LocalDateTime lastModified;

}
