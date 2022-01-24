package com.makridin.instazoo.validators;

import com.makridin.instazoo.payload.request.SignupRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PasswordMatchesValidatorTest {

    private static PasswordMatchesValidator validator;

    @BeforeAll
    private static void setUp() {
        validator = new PasswordMatchesValidator();
    }

    @Test
    public void testIsValid() {
        Assertions.assertTrue(validator.isValid(getSignupRequest("test", "test"), null));
    }

    @Test
    public void testIsValidPasswordNull() {
        Assertions.assertFalse(validator.isValid(getSignupRequest(null, "test"), null));
    }

    @Test
    public void testIsValidPasswordNullAndConfirmNull() {
        Assertions.assertFalse(validator.isValid(getSignupRequest(null, null), null));
    }

    @Test
    public void testIsValidConfirmNull() {
        Assertions.assertFalse(validator.isValid(getSignupRequest("test", null), null));
    }

    @Test
    public void testIsValidOneLetterInCapital() {
        Assertions.assertFalse(validator.isValid(getSignupRequest("test", "Test"), null));
    }

    @Test
    public void testIsValidOneLetterInRussianLanguage() {
        Assertions.assertFalse(validator.isValid(getSignupRequest("test", "tеst"), null));
    }


    private SignupRequest getSignupRequest(String password, String confirmPassword) {
        return SignupRequest.builder()
                .password(password)
                .confirmPassword(confirmPassword)
                .build();
    }
}
