package com.tnc.shelter.repository.interfaces;

import com.tnc.shelter.repository.entities.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Shelter findByName(String shelter);
}
