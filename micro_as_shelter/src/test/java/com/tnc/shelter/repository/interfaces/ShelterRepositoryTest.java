package com.tnc.shelter.repository.interfaces;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@RequiredArgsConstructor
class ShelterRepositoryTest {

    private final ShelterRepository shelterTest;

}