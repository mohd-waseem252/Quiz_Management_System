package com.tachnique.app.quiz_service_impl.mapper;

import com.tachnique.app.dto.UserDto;
import com.tachnique.app.quiz_service_impl.entity.UserEntity;
import com.tachnique.app.quiz_service_impl.enums.UserRole;

public class UserMapper {
    public static UserEntity toEntity(UserDto dto) {
        if (dto == null) return null;
        return UserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole() != null ? UserRole.valueOf(dto.getRole()) : null)
                .build();
    }
    public static UserDto toDto(UserEntity entity) {
        if (entity == null) return null;
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .role(entity.getRole() != null ? entity.getRole().name() : null)
                .build();
    }
}
