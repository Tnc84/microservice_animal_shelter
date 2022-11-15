package com.tnc.animals.service.mapper;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.service.domain.AnimalDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalDomainMapper {

//    @Mapping(source = "id", target = "id")
    Animal toEntity(AnimalDomain animalDomain);

//    @Mapping(source = "id", target = "id")
    List<Animal> toEntityList(List<AnimalDomain> animalDomainList);

//    @Mapping(source = "id", target = "id")
    AnimalDomain toDomain(Animal animal);

//    @Mapping(source = "id", target = "id")
    List<AnimalDomain> toDomainList(List<Animal> animal);
}
;