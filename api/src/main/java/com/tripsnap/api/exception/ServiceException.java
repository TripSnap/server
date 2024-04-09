package com.tripsnap.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException{
    public ServiceException() {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(HttpStatus status) {
        this.status = status;
    }


    public static ServiceException BadRequestException() {
        return new ServiceException(HttpStatus.BAD_REQUEST);
    }

    public static ServiceException PermissionDenied() {
        return new ServiceException(HttpStatus.FORBIDDEN);
    }

    @Getter
    private HttpStatus status;
}
