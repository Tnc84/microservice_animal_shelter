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
    private String environment;

}
