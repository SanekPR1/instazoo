package com.makridin.instazoo.validators;

import com.makridin.instazoo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UniqueUsernameValidatorTest {

    private static final String EXISTED_USERNAME = "test";
    private static final String NEW_USERNAME = "new_test";

    @Mock
    private static UserRepository userRepository;

    @InjectMocks
    private static UniqueUsernameValidator validator;

    @Test
    public void testIsValidExistedEmail() {
        when(userRepository.existsByUsername(EXISTED_USERNAME)).thenReturn(true);
        Assertions.assertFalse(validator.isValid(EXISTED_USERNAME, null));
    }

    @Test
    public void testIsValidNonExistentEmail() {
        when(userRepository.existsByUsername(NEW_USERNAME)).thenReturn(false);
        Assertions.assertTrue(validator.isValid(NEW_USERNAME, null));
    }
}
