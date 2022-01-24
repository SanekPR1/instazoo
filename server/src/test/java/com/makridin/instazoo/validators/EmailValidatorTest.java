package com.makridin.instazoo.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmailValidatorTest {

    private static EmailValidator validator;

    @BeforeAll
    private static void setUp() {
        validator = new EmailValidator();
    }

    @Test
    public void testIsValid() {
        Assertions.assertTrue(validator.isValid("test@mail.com", null));
    }

    @Test
    public void testIsValidWrongTailFormat() {
        Assertions.assertFalse(validator.isValid("test@mail.c", null));
    }

    @Test
    public void testIsValidWithoutAtSymbol() {
        Assertions.assertFalse(validator.isValid("testmail.com", null));
    }

    @Test
    public void testIsValidWithNullEmail() {
        Assertions.assertFalse(validator.isValid(null, null));
    }

    @Test
    public void testIsValidWithWhitspacesEmail() {
        Assertions.assertFalse(validator.isValid("      ", null));
    }
}
