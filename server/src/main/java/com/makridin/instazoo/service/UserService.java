package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.UserDTO;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.entity.enums.Roles;
import com.makridin.instazoo.exceptions.UserExistException;
import com.makridin.instazoo.payload.request.SignupRequest;
import com.makridin.instazoo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignupRequest request) {
        User user = getUser(request);

        try {
            LOG.info("Saving user {}", user);
            return userRepository.save(user);
        } catch (Exception ex) {
            LOG.error("error during registration. {}", ex.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " is already existed");
        }
    }

    public UserDTO updateUser(UserDTO userDto, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setBio(userDto.getBio());
        return userToUserDto(userRepository.save(user));
    }

    public UserDTO getCurrentUser(Principal principal) {
        return userToUserDto(getUserByPrincipal(principal));
    }

    public UserDTO getUserById(Long userId) {
        return userToUserDto(userRepository.findUserById(userId)
                .orElseThrow(() -> new UserExistException("User doesn't exist. User id=" + userId)));
    }

    protected User getUserByPrincipal(Principal principal) {
        return userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User with that username wasn't found"));
    }

    private User getUser(SignupRequest request) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .lastname(request.getLastname())
                .firstname(request.getFirstname())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Roles.ROLE_USER))
                .build();
    }

    private UserDTO userToUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .bio(user.getBio())
                .build();
    }
}
