package com.perfectkode.bikri.common.mapper;

import com.perfectkode.bikri.auth.dto.response.UserResponse;
import com.perfectkode.bikri.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserResponse> {

    @Override
    public UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isVerified(),
                user.getRole() != null ? user.getRole().getRoleName() : null
        );
    }

    @Override
    public User toEntity(UserResponse dto) {
        // Typically unused for incoming requests since DTOs map to entities via specific Service logic
        throw new UnsupportedOperationException("Direct DTO to Entity mapping is not supported for UserResponse");
    }
}
