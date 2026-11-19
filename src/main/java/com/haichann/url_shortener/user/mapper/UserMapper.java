package com.haichann.url_shortener.user.mapper;

import com.haichann.url_shortener.user.dto.ProfileDTO;
import com.haichann.url_shortener.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.roleName")
    ProfileDTO toProfileDTO(User user);
}
