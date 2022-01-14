package com.makridin.instazoo.annotations;

import com.makridin.instazoo.validators.UniqueEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "This email is already used";

    Class<?>[] groups() default{};

    Class<? extends Payload>[]  payload() default{};
}
