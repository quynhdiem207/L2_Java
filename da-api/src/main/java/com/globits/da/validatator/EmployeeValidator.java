package com.globits.da.validatator;

import com.globits.da.domain.Employee;
import com.globits.da.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class EmployeeValidator {

    @Autowired
    EmployeeRepository repos;

    public boolean isRequired(String data) {
        return (data == null || !StringUtils.hasText(data)) ? false : true;
    }

    public boolean hasSpace(String data) {
        return data.contains(" ");
    }

    public boolean isCodeLengthValid(String code) {
        return (code.length() < 6 || code.length() > 10) ? false : true;
    }

    public boolean isCodeDuplicate(String code, Employee employee) {
        if (employee != null && code.equals(employee.getCode())) {
            return false;
        } else if(repos.countByCode(code) == 0) {
            return false;
        }
        return true;
    }

    public boolean isEmailValid(String email) {
        String pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"
            + "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
            + "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?"
            + "[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-"
            + "\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        return Pattern.matches(pattern, email);
    }

    public boolean isPhoneValid(String phone) {
        String pattern = "^\\d{8,10}$";
        return Pattern.matches(pattern, phone);
    }

    public boolean isAgeValid(Integer age) {
        return age == null ? false : age < 0 ? false : true;
    }
}
