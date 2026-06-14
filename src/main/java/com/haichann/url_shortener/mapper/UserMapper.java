package com.haichann.url_shortener.mapper;

import com.haichann.url_shortener.model.dto.response.ProfileDTO;
import com.haichann.url_shortener.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.roleName")
    ProfileDTO toProfileDTO(User user);
}
