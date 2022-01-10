package com.makridin.instazoo.validators;

import com.makridin.instazoo.annotations.UniqueUsername;
import com.makridin.instazoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepository.existsByUsername(userName);
    }
}
