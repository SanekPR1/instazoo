package com.makridin.instazoo.service;

import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.entity.enums.Roles;
import com.makridin.instazoo.exceptions.UserExistException;
import com.makridin.instazoo.payload.request.SignupRequest;
import com.makridin.instazoo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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

    private User getUser(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Roles.ROLE_USER);
        return user;
    }
}
