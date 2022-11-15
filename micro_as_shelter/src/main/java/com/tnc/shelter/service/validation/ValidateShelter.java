package com.tnc.shelter.service.validation;

import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class ValidateShelter {

    @Autowired
    static ShelterDomainMapper shelterDomainMapper;

    @Autowired
    static ShelterService shelterService;

    public static void validateShelter(ShelterDomain shelterDomain, String name) throws ShelterAddressException, ShelterNameException {
//        var findShelterByName = shelterDomainMapper.toDomain(shelterDomainMapper.toEntity(shelterService.getShelterByName()));
//        var findShelterByName = shelterService.getShelterByName();
//        var findShelterByName = shelterService.getShelterByName();
//TODO Check if the name contains only letters
        if (!shelterDomain.getCity().toLowerCase(Locale.ROOT).contains("iasi")) {
            throw new ShelterAddressException("The shelter is not from Iasi");
        }
        /// this method must iterate to all shelters name and compare the names
//        check if this name exists in db
//        if (shelterDomain.getName().contains(findShelterByName.getName())) {
//            throw new ShelterNameException("This name already exist");
//        }
    }
}
