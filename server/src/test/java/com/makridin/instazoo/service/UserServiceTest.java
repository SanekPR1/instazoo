package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.UserDTO;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.entity.enums.Roles;
import com.makridin.instazoo.exceptions.UserExistException;
import com.makridin.instazoo.payload.request.SignupRequest;
import com.makridin.instazoo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Principal;
import java.sql.SQLDataException;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService service;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository repository;
    @Mock
    private Principal principal;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    private void setUp() {
        when(principal.getName()).thenReturn("Test");
    }

    @Test
    public void testCreateUser() {
        SignupRequest request = getSignupRequest();

        when(passwordEncoder.encode(request.getPassword())).thenReturn(encryptPassword(request.getPassword()));
        when(repository.save(any())).thenReturn(getUser());

        service.createUser(request);

        verify(repository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    public void testCreateUserSaveError() {
        SignupRequest request = getSignupRequest();

        when(passwordEncoder.encode(request.getPassword())).thenReturn(encryptPassword(request.getPassword()));
        when(repository.save(any())).thenThrow(new RuntimeException());

        Assertions.assertThrows(UserExistException.class, () -> service.createUser(request));

        verify(repository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    public void testUpdateUser() {
        when(repository.findUserByUsername("Test")).thenReturn(Optional.of(getUser()));
        when(repository.save(any())).thenReturn(getUser());

        UserDTO user = service.updateUser(getUserDTO(), principal);

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findUserByUsername("Test");
        Assertions.assertNotNull(user);
    }

    @Test
    public void testGetUserById() {
        User expected = getUser();
        when(repository.findUserById(1L)).thenReturn(Optional.of(expected));

        UserDTO user = service.getUserById(1L);

        verify(repository, times(1)).findUserById(1L);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(1L, user.getId());
        Assertions.assertEquals(expected.getFirstname(), user.getFirstname());
        Assertions.assertEquals(expected.getLastname(), user.getLastname());
        Assertions.assertEquals(expected.getUsername(), user.getUsername());
        Assertions.assertEquals(expected.getBio(), user.getBio());
        Assertions.assertNull(user.getEmail());
    }

    @Test
    public void testGetUserByIdNoUser() {
        when(repository.findUserById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserExistException.class, () -> service.getUserById(1L));

        verify(repository, times(1)).findUserById(1L);
    }

    @Test
    public void testGetUserByPrincipal() {
        when(repository.findUserByUsername("Test")).thenReturn(Optional.of(getUser()));

        User user = service.getUserByPrincipal(principal);

        verify(repository, times(1)).findUserByUsername("Test");
        Assertions.assertNotNull(user);
    }

    @Test
    public void testGetUserByPrincipalNoUser() {
        when(repository.findUserByUsername("Test")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> service.getUserByPrincipal(principal));

        verify(repository, times(1)).findUserByUsername("Test");
    }

    private SignupRequest getSignupRequest() {
        return SignupRequest.builder()
                .firstname("Test")
                .lastname("Test")
                .username("Test")
                .email("test@test.io")
                .password("Test")
                .confirmPassword("Test")
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("Test")
                .username("Test")
                .password("Test")
                .email("test@test.io")
                .roles(Set.of(Roles.ROLE_USER))
                .build();
    }

    private UserDTO getUserDTO() {
        return UserDTO.builder()
                .firstname("Test")
                .lastname("Test")
                .bio("bio")
                .build();
    }

    private String encryptPassword(String password) {
        return encoder.encode(password);
    }
}
