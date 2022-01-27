package com.makridin.instazoo.service;

import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.entity.enums.Roles;
import com.makridin.instazoo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository repository;

    @Test
    public void testLoadUserByUsername() {
        User expected = getUser();
        when(repository.findUserByUsername("Test")).thenReturn(Optional.of(getUser()));

        User user = (User) service.loadUserByUsername("Test");

        verify(repository, times(1)).findUserByUsername("Test");
        checkUserDetails(expected, user);
        Assertions.assertEquals(1, user.getAuthorities().size());
    }

    @Test
    public void testLoadUserByUsernameNonExisted() {
        when(repository.findUserByUsername("Test")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("Test"));

        verify(repository, times(1)).findUserByUsername("Test");
    }

    @Test
    public void testLoadUserById() {
        User expected = getUser();
        when(repository.findUserById(1L)).thenReturn(Optional.of(getUser()));

        User user = service.loadUserById(1L);

        verify(repository, times(1)).findUserById(1L);
        checkUserDetails(expected, user);
        Assertions.assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testLoadUserByIdNonExisted() {
        when(repository.findUserById(1L)).thenReturn(Optional.empty());

        User user = service.loadUserById(1L);

        verify(repository, times(1)).findUserById(1L);
        Assertions.assertNull(user);
    }

    private void checkUserDetails(User expected, User user) {
        Assertions.assertEquals(expected.getId(), user.getId());
        Assertions.assertEquals(expected.getUsername(), user.getUsername());
        Assertions.assertEquals(expected.getEmail(), user.getEmail());
        Assertions.assertEquals(expected.getPassword(), user.getPassword());
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .username("Test")
                .email("test@test.com")
                .password("test")
                .roles(Set.of(Roles.ROLE_ADMIN))
                .build();
    }

}
