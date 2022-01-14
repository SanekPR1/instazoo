package com.makridin.instazoo.facade;

import com.makridin.instazoo.dto.UserDTO;
import com.makridin.instazoo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .bio(user.getBio())
                .build();
    }
}
