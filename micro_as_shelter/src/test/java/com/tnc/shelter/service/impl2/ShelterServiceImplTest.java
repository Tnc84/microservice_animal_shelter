package com.tnc.shelter.service.impl2;//package com.tnc.shelter.service.impl;
//
//import com.tnc.shelter.repository.interfaces.ShelterRepository;
//import com.tnc.shelter.service.domain.ShelterDomain;
//import com.tnc.shelter.service.mapper.ShelterDomainMapper;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//@ExtendWith(MockitoExtension.class)
//class ShelterServiceImplTest {
//
//    private final ShelterDomain shelterDomain = new ShelterDomain(3L, "Bucium", "Iasi", "env");
//
//    @Mock
//    private ShelterRepository shelterRepositoryMock;
//    @Mock
//    private ShelterDomainMapper shelterDomainMapper;
//    @InjectMocks
//    private ShelterServiceImpl shelterServiceMock;
//
////    @BeforeEach
////    void init(){
////        shelterRepositoryMock = Mockito.mock(ShelterRepository.class);
////        shelterServiceMock = new ShelterServiceImpl(shelterRepositoryMock);
////    }
//
//    @Test
//    @Disabled
//    void get() {
//    }
//
//    @Test
//    void getAll() {
//    }
//
//    @Test
//    @Disabled
//    void add() {
//    }
//
//    @Test
//    @Disabled
//    void update() {
//        assertNotEquals(1L, shelterDomain.getId());
//
//        assertThat(shelterDomain).isNotNull();
//        assertThat(shelterDomain.getName()).isNotBlank();
//        assertThat(shelterDomain.getId()).isEqualTo(3L);
//    }
//
//    @Test
//    @Disabled
//    void validateShelter() {
//    }
//}