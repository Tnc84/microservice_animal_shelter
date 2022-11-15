package com.tnc.userManagement.service.mapper;

import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.model.UserDomain;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDomainMapper {

    UserDomain toDomain(User user);

    User toEntity(UserDomain userDomain);

    List<User> toEntityList(List<UserDomain> userDomainList);

    List<UserDomain> toDomainList(List<User> userList);

}
