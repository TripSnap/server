package com.tripsnap.api.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsnap.api.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * RequestParam으로 넘어오는 Map에 들어있는 값 검사하려고 만듬
 * */
public class ParameterUtil {
    private static ValidatorFactory validatorFactory;
    private static ObjectMapper objectMapper;

    static {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> void validation(Map<String, String> param, Class<T> tClass)  {
        T converted = objectMapper.convertValue(param, tClass);
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violationSet =  validator.validate(converted);
        if(violationSet.size() > 0) {
            throw ServiceException.BadRequestException();
        }
    }

    public static void validation(String value, ValidationType type) {
        if(!StringUtils.hasText(value)) {
            throw ServiceException.BadRequestException();
        }
        try {
            var instance = type._class.getConstructor(String.class).newInstance(value);
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> violationSet =  validator.validate(instance);
            if(violationSet.size() > 0) {
                throw ServiceException.BadRequestException();
            }
        } catch (NoSuchMethodException | InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw new ServiceException();
        }
    }

}