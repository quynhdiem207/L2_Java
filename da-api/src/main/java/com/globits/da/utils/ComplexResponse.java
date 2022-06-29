package com.globits.da.utils;

import java.util.List;

public class ComplexResponse<T> {
    public T data;
    public int statusCode;
    public List<String> errorMessages;

    public ComplexResponse() {}

    public ComplexResponse(T data, int statusCode, List<String> errorMessages) {
        this.data = data;
        this.statusCode = statusCode;
        this.errorMessages = errorMessages;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
