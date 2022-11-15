package com.tnc.animals.service.interfaces;

import com.tnc.animals.service.domain.AnimalDomain;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public interface AnimalService {

    AnimalDomain get(Long id);

    List<AnimalDomain> getAll();

    AnimalDomain add(@Valid AnimalDomain animalDomain);

    AnimalDomain update(@Valid AnimalDomain animalDomain);
}
