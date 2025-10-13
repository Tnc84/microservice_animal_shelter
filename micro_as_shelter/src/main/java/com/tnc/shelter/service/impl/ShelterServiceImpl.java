package com.tnc.shelter.service.impl;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import com.tnc.shelter.service.validation.ValidateShelter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@Data
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository shelterRepository;
    private final ShelterDomainMapper shelterDomainMapper;


    @Override
    // TODO this method must receive a String name
    public ShelterDomain getShelterByName() {
        return shelterDomainMapper.toDomain(shelterRepository.findByName("Bucium"));
    }

//    @Override
//    public ShelterDomain getShelterById(Long id) {
//        return shelterDomainMapper.toDomain(shelterRepository.getById(id));
//    }

    @Override
    public List<ShelterDomain> getAll() {
        return shelterDomainMapper.toDomainList(shelterRepository.findAll());
    }

    @Override
    public ShelterDomain add(ShelterDomain shelterDomain) throws ShelterAddressException, ShelterNameException {
        ValidateShelter.validateShelter(shelterDomain, shelterDomain.getName());
        Shelter addShelter = shelterDomainMapper.toEntity(shelterDomain);
        return shelterDomainMapper.toDomain(shelterRepository.save(addShelter));
    }

    @Override
    public ShelterDomain update(ShelterDomain shelterDomain) {
        Shelter updatedShelter = shelterDomainMapper.toEntity(shelterDomain);
        return shelterDomainMapper.toDomain(shelterRepository.save(updatedShelter));
    }

    public ShelterDomain findByName(String name) {
        return shelterDomainMapper.toDomain(shelterRepository.findByName(name));
    }

}
