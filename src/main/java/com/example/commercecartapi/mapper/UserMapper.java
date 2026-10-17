package com.example.commercecartapi.mapper;

import com.example.commercecartapi.dto.UserResponse;
import com.example.commercecartapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
