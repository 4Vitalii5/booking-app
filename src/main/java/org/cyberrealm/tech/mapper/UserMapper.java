package org.cyberrealm.tech.mapper;

import org.cyberrealm.tech.config.MapperConfig;
import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserRegistrationRequestDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    void updateUser(@MappingTarget User user, UserInfoUpdateDto updateDto);
}
