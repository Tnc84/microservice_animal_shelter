package com.tnc.userManagement.controller.dtoMapper;

import com.tnc.userManagement.controller.dto.UserDTO;
import com.tnc.userManagement.service.model.UserDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "notLocked", target = "isNotLocked")
    UserDTO toDTO(UserDomain userDomain);

    @Mapping(source = "isActive", target = "active")
    @Mapping(source = "isNotLocked", target = "notLocked")
    UserDomain toDomain(UserDTO userDTO);

    List<UserDomain> toDomainList(List<UserDTO> userDTOList);

    List<UserDTO> toDTOList(List<UserDomain> userDomainList);

}
