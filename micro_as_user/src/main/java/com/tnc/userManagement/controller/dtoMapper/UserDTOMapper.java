package com.tnc.userManagement.controller.dtoMapper;

import com.tnc.userManagement.controller.dto.UserDTO;
import com.tnc.userManagement.service.model.UserDomain;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toDTO(UserDomain userDomain);

    UserDomain toDomain(UserDTO userDTO);

    List<UserDomain> toDomainList(List<UserDTO> userDTOList);

    List<UserDTO> toDTOList(List<UserDomain> userDomainList);

}
