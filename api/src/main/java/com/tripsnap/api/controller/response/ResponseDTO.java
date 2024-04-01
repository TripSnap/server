package com.tripsnap.api.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class ResponseDTO {

    public static ResponseDTO.SuccessOrNot SuccessOrNot(boolean success, String message) {
        return new SuccessOrNot(success, message);
    }
    public static ResponseDTO.SimpleSuccessOrNot SuccessOrNot(boolean success) {
        return new SimpleSuccessOrNot(success);
    }
    public static <T> ResponseDTO.WithData<T> WithData(T data, String message) {
        return new WithData<>(data, message);
    }
    public static <T> ResponseDTO.SimpleWithData<T> WithData(T data) {
        return new SimpleWithData<>(data);
    }
    public static <T> ResponseDTO.WithPageData<T> WithPageData(Pageable pageable, T data, String message) {
        return new WithPageData<>(pageable.getPageNumber(), pageable.getPageSize(), data,message);
    }
    public static <T> ResponseDTO.SimpleWithPageData<T> WithPageData(Pageable pageable, T data) {
        return new SimpleWithPageData<>(pageable.getPageNumber(), pageable.getPageSize(), data);
    }

    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class SuccessOrNot {
        private boolean success;
        private String message;
    }
    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class WithData<T> {
        private T data;
        private String message;
    }
    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class WithPageData<T> {
        private int page;
        private int size;
        private T data;
        private String message;
    }
    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class SimpleSuccessOrNot {
        private boolean success;
    }
    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class SimpleWithData<T> {
        private T data;
    }
    @Getter
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static class SimpleWithPageData<T> {
        private int page;
        private int size;
        private T data;
    }
}
