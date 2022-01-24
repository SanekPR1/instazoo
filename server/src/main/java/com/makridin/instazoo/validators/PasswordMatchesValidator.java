package com.makridin.instazoo.validators;

import com.makridin.instazoo.annotations.PasswordMatches;
import com.makridin.instazoo.payload.request.SignupRequest;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        SignupRequest signupRequest = (SignupRequest)obj;
        if(!StringUtils.hasText(signupRequest.getPassword())) {
            return false;
        }
        return signupRequest.getPassword().equals(signupRequest.getConfirmPassword());
    }
}
