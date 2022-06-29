package com.globits.da.utils;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class Response<T> {
    private T data;
    private int statusCode;
    private String errorMessage;

    public Response(T data, int statusCode, String errorMessage) {
        this.data = data;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}