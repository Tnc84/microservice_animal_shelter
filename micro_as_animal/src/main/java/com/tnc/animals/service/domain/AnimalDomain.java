package com.tnc.animals.service.domain;

import lombok.Data;

@Data
public class AnimalDomain {
    private Long id;
    private String name;
    private String breed;
    private String species;
    private String photo;
    private String environment;
}
