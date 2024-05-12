package com.tripsnap.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    private ValidEnum annotation;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;
        Enum[] enumValues = this.annotation.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Enum enumValue : enumValues) {
                if (enumValue.name().equals(value)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }
}
