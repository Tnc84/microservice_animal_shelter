package com.tnc.shelter.service.impl;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.service.CircuitBreakerService;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import com.tnc.shelter.service.validation.ValidateShelter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Data
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final CircuitBreakerService circuitBreakerService;
    private final ShelterDomainMapper shelterDomainMapper;


    @Override
    public ShelterDomain getShelterByName() {
        return shelterDomainMapper.toDomain(circuitBreakerService.findByName("Bucium").orElse(null));
    }

   @Override
   public ShelterDomain getShelterById(Long id) {
       return shelterDomainMapper.toDomain(circuitBreakerService.findById(id).orElse(null));
   }

    @Override
    public List<ShelterDomain> getAll() {
        return shelterDomainMapper.toDomainList(circuitBreakerService.getAllShelters());
    }

    @Override
    public ShelterDomain add(ShelterDomain shelterDomain) throws ShelterAddressException, ShelterNameException {
        ValidateShelter.validateShelter(shelterDomain, shelterDomain.getName());
        Shelter addShelter = shelterDomainMapper.toEntity(shelterDomain);
        return shelterDomainMapper.toDomain(circuitBreakerService.saveShelter(addShelter));
    }

    @Override
    public ShelterDomain update(ShelterDomain shelterDomain) {
        Shelter updatedShelter = shelterDomainMapper.toEntity(shelterDomain);
        return shelterDomainMapper.toDomain(circuitBreakerService.saveShelter(updatedShelter));
    }

    public ShelterDomain findByName(String name) {
        return shelterDomainMapper.toDomain(circuitBreakerService.findByName(name).orElse(null));
    }

}
