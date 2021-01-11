package com.playground.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = StockTypeValidator.class)
@Documented
public @interface StockType {

    String message() default "Invalid stock type value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
