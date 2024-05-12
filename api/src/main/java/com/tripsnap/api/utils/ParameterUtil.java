package com.tripsnap.api.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tripsnap.api.domain.dto.option.FriendListOption;
import com.tripsnap.api.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.util.StringUtils;

import java.lang.constant.Constable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * RequestParam으로 넘어오는 Map에 들어있는 값 검사하려고 만듬
 * */
public class ParameterUtil {
    private final static ValidatorFactory validatorFactory;
    private final static ObjectMapper objectMapper;
    private final static Gson gson;

    static {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        gson = new Gson();
    }

    /**
     * param을 tClass 타입으로 변환 후 constraints 검사
     * */
    public static <T> T validationAndConvert(Map<String, Object> param, Class<T> tClass)  {
        try {
            T converted = objectMapper.convertValue(param, tClass);
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<T>> violationSet =  validator.validate(converted);
            if(violationSet.size() > 0) {
                throw ServiceException.BadRequestException();
            }
            return converted;
        } catch (IllegalArgumentException e) {
            throw ServiceException.BadRequestException();
        }
    }
    public static <T> void validation(Map<String, Object> param, Class<T> tClass)  {
        validationAndConvert(param, tClass);
    }

    /**
     * Map{@literal <String}, Object> 형태에서 ValidationType 클래스 안의 값 추출
     * */
    public static Object convert(Object value, TypeToken<?> typeToken)  {
        try {
            String json = gson.toJson(value);
            return gson.fromJson(json, typeToken.getType());
        } catch (IllegalArgumentException e) {
            throw ServiceException.BadRequestException();
        }
    }

    /**
     * Map{@literal <String}, Object> 형태에서
     * ValidationType.PrimitiveWrapper 타입인지 검사 후
     * type 값으로 변환
     * */

    public static <T extends Constable> T validationAndConvert(Object value, ValidationType.PrimitiveWrapper validationType, Class<T> type) {
        try {
            if(Long.class.equals(type)) {
                Long checkValue = Long.valueOf(String.valueOf(value));
                ParameterUtil.validationAndConvert(checkValue, validationType);
                return type.cast(checkValue);
            }
            if(FriendListOption.class.equals(type)) {
                String s = ParameterUtil.validationAndConvert(String.valueOf(value), validationType);
                return type.cast(FriendListOption.valueOf(s));
            }
        } catch (IllegalArgumentException e) {
            throw ServiceException.BadRequestException();
        }
        throw ServiceException.BadRequestException();
    }

    /**
     * Map{@literal <String}, Object> 형태에서
     * ValidationType.PrimitiveWrapper 타입인지 검사 후
     * String으로 변환
     * */
    public static <T> String validationAndConvert(T value, ValidationType.PrimitiveWrapper validationType) {
        if(value == null || !StringUtils.hasText(String.valueOf(value))) {
            throw ServiceException.BadRequestException();
        }
        try {
            var instance = validationType._class.getConstructor(value.getClass()).newInstance(value);
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> violationSet =  validator.validate(instance);
            if(violationSet.size() > 0) {
                throw ServiceException.BadRequestException();
            }
            return String.valueOf(instance);
        } catch (NoSuchMethodException | InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw ServiceException.BadRequestException();
        }
    }

    public static <T> void validation(T value, ValidationType.PrimitiveWrapper validationType) {
        validationAndConvert(value, validationType);
    }

}
