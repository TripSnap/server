package com.tripsnap.api.domain;

import com.tripsnap.api.domain.dto.JoinDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ValidatorTest {

    @Test
    void test() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        JoinDTO joinDTO = new JoinDTO("test2@naver.", "fgaij#$#opkfg","fddg4");
        Set<ConstraintViolation<JoinDTO>> validate = validator.validate(joinDTO);
    }
}
