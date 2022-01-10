package com.makridin.instazoo.annotations;

import com.makridin.instazoo.validators.UniqueEmailValidator;
import com.makridin.instazoo.validators.UniqueUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Documented
public @interface UniqueUsername {
    String message() default "This username is already used";

    Class<?>[] groups() default{};

    Class<? extends Payload>[]  payload() default{};
}
