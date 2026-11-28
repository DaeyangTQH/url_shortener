package com.haichann.url_shortener.user.mapper;

import com.haichann.url_shortener.user.dto.ProfileDTO;
import com.haichann.url_shortener.user.dto.UpdateMyInfoRequest;
import com.haichann.url_shortener.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.roleName")
    ProfileDTO toProfileDTO(User user);

    @BeanMapping(ignoreByDefault = true, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "avatarImageUrl", source = "avatarImageUrl")
    void updateProfile(UpdateMyInfoRequest request, @MappingTarget User user);
}
