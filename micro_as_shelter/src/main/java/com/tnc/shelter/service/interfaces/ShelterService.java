package com.tnc.shelter.service.interfaces;

import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShelterService {
//    ShelterDomain get(Long id);
    ShelterDomain getShelterByName();

    List<ShelterDomain> getAll();

    ShelterDomain add(ShelterDomain shelterDomain) throws ShelterAddressException, ShelterNameException;

    ShelterDomain update(ShelterDomain shelterDomain);

}
