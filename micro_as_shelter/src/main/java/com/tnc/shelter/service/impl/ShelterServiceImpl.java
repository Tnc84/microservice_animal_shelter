package com.tnc.shelter.service.impl;

import com.sun.xml.bind.v2.TODO;
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
import org.springframework.core.env.Environment;
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
    private final Environment environment;


    @Override
    // TODO this method must receive a String name
    public ShelterDomain getShelterByName() {
        var getShelter = shelterDomainMapper.toDomain(shelterRepository.findByName("Bucium"));
        setShelterEnvironment(getShelter);
        return getShelter;
    }

//    @Override
//    public ShelterDomain getShelterById(Long id) {
//        return shelterDomainMapper.toDomain(shelterRepository.getById(id));
//    }

    @Override
    public List<ShelterDomain> getAll() {
        String port = environment.getProperty("local.server.port");
        var getShelters = shelterDomainMapper.toDomainList(shelterRepository.findAll());

        for (ShelterDomain shelters : getShelters) {
            shelters.setEnvironment(port);
        }
        return getShelters;
    }

    @Override
    public ShelterDomain add(ShelterDomain shelterDomain) throws ShelterAddressException, ShelterNameException {
        ValidateShelter.validateShelter(shelterDomain, shelterDomain.getName());
        shelterDomain = setShelterEnvironment(shelterDomain);
//        Shelter addShelter = setShelterEnvironment(shelterDomain);
//        return shelterDomainMapper.toDomain(shelterRepository.save(addShelter));
        return shelterDomainMapper.toDomain(shelterDomainMapper.toEntity(shelterDomain));
    }

    @Override
    public ShelterDomain update(ShelterDomain shelterDomain) {
        return shelterDomainMapper.toDomain(shelterDomainMapper.toEntity(shelterDomain));
    }

    public ShelterDomain findByName(String name) {
        var getShelter = shelterDomainMapper.toDomain(shelterRepository.findByName(name));
        setShelterEnvironment(getShelter);
        return getShelter;
    }

    private ShelterDomain setShelterEnvironment(ShelterDomain shelterDomain) {
        String port = environment.getProperty("local.server.port");
        shelterDomain.setEnvironment(port);
        return shelterDomain;
    }
}
