package com.tnc.animals.service.impl;

import com.tnc.animals.repository.interfaces.AnimalRepository;
import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.animals.service.interfaces.AnimalService;
import com.tnc.animals.service.mapper.AnimalDomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalDomainMapper animalDomainMapper;
    private final Environment environment;

    @Override
    public AnimalDomain get(Long id) {
        return animalDomainMapper.toDomain(animalRepository.getById(id));
    }

    @Override
    public List<AnimalDomain> getAll() {
        String port = environment.getProperty("local.server.port");
        var getAnimals = animalDomainMapper.toDomainList(animalRepository.findAll());
        for (AnimalDomain animals : getAnimals
             ) {
            animals.setEnvironment(port);
        }
        return getAnimals;
    }

    @Override
    public AnimalDomain add(AnimalDomain animalDomain) {
        return animalDomainMapper.toDomain(animalRepository.save(animalDomainMapper.toEntity(animalDomain)));
    }

    @Override
    public AnimalDomain update(AnimalDomain animalDomain) {
        return animalDomainMapper.toDomain(animalRepository.save(animalDomainMapper.toEntity(animalDomain)));
    }

    private AnimalDomain setEnvironment(AnimalDomain animalDomain) {
        String port = environment.getProperty("local.server.port");
        animalDomain.setEnvironment(port);
        return animalDomain;
    }
}
