package com.globits.da.utils;

public enum ErrorMessage {
    CODE_IS_NULL(40001, "Code is required"),
    CODE_CONTAIN_SPACE(40002, "Code can't contain space"),
    CODE_LENGTH_INVALID(40003, "Code must be 6 - 10 characters"),
    CODE_IS_EXIST(40004, "Code is already existed"),
    NAME_IS_NULL(40005, "Name is required"),
    EMAIL_IS_NULL(40006, "Email is required"),
    EMAIL_IS_INVALID(40007, "Email is invalid"),
    PHONE_IS_NULL(40008, "Phone is required"),
    PHONE_IS_INVALID(40009, "Phone must not be greater than 11 characters, and only contain number characters"),
    AGE_IS_INVALID(40011, "Age must not be nagative"),
    COMMUNE_IS_NULL(40012, "Commune is required"),
    COMMUNE_NOT_EXIST(40013, "Commune does not exist"),
    COMMUNE_IS_INVALID(40014, "Commune is not in the district"),
    DISTRICT_IS_NULL(40015, "District is required"),
    DISTRICT_NOT_EXIST(40016, "District does not exist"),
    DISTRICT_IS_INVALID(40017, "District is not in the province"),
    PROVINCE_IS_NULL(40018, "Province is required"),
    PROVINCE_NOT_EXIST(40019, "Province does not exist"),
    ID_IS_NULL(40020, "ID is required"),
    CERTIFICATE_NOT_EXIST(40021, "Certificate does not exist"),
    EMPLOYEE_NOT_EXIST(40022, "Employee does not exist"),
    PATH_VARIABLE_ID_INVALID(40099, "ID is invalid"),
    SERVER_ERROR(50000, "Server error"),
    SUCCESS(200, "Success")
    ;

    private final String message;
    private final int code;

    ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
