package com.tnc.shelter.service.impl;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShelterServiceImplTest {

    private final ShelterDomain shelterDomain = new ShelterDomain(3L, "Bucium", "Iasi");

    @Mock
    private ShelterRepository shelterRepositoryMock;
    @Mock
    private ShelterDomainMapper shelterDomainMapper;
    @InjectMocks
    private ShelterServiceImpl shelterServiceMock;

    @Test
    @Disabled
    void get() {
    }

    @Test
    void getAll() {
        shelterServiceMock.getAll();
        assertThat(shelterRepositoryMock.findAll());
    }

    @Test
    @Disabled
    void add() throws ShelterAddressException, ShelterNameException {
        ShelterDomain shelterDomain = new ShelterDomain(3L, "Bucium", "iasi");

        shelterServiceMock.add(shelterDomain);
//        shelterRepositoryMock.save(shelterDomainMapper.toEntity(shelterDomain));
        ArgumentCaptor<ShelterDomain> shelterArgCaptor =
                ArgumentCaptor.forClass(ShelterDomain.class);
        verify(shelterRepositoryMock.save(shelterDomainMapper.toEntity(shelterDomain)));
//                .save(shelterArgCaptor.capture());
        ShelterDomain capturedShelter = shelterArgCaptor.getValue();
        assertThat(capturedShelter).isEqualTo(shelterDomain);
    }

    @Test
    @Disabled
    void update() {
        assertNotEquals(1L, shelterDomain.getId());

        assertThat(shelterDomain).isNotNull();
        assertThat(shelterDomain.getName()).isNotBlank();
        assertThat(shelterDomain.getId()).isEqualTo(3L);
    }

    @Test
    @Disabled
    void validateShelter() {
    }
}