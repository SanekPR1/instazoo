package com.makridin.instazoo.validators;

import com.makridin.instazoo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UniqueEmailValidatorTest {


    private static final String EXISTED_EMAIL = "hello@hello.io";
    private static final String NEW_EMAIL = "test@mail.com";

    @Mock
    private static UserRepository userRepository;

    @InjectMocks
    private static UniqueEmailValidator validator;

    @Test
    public void testIsValidExistedEmail() {
        when(userRepository.existsByEmail(EXISTED_EMAIL)).thenReturn(true);
        Assertions.assertFalse(validator.isValid(EXISTED_EMAIL, null));
    }

    @Test
    public void testIsValidNonExistentEmail() {
        when(userRepository.existsByEmail(NEW_EMAIL)).thenReturn(false);
        Assertions.assertTrue(validator.isValid(NEW_EMAIL, null));
    }
}
