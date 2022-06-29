package com.globits.da.rest;

import com.globits.da.dto.EmployeeCertificateDto;
import com.globits.da.service.EmployeeCertificateService;
import com.globits.da.utils.ComplexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee_certificates")
public class RestEmployeeCertificateController {

    @Autowired
    EmployeeCertificateService service;

    @PostMapping
    public ComplexResponse<EmployeeCertificateDto> issueCertificate(
            @RequestBody @Valid EmployeeCertificateDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ComplexResponse<>(
                null,
                400,
                bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()).collect(Collectors.toList())
            );
        }

        try {
            List<List<UUID>> result =  service.issueCertificate(dto);
            List<String> errorMessages = new ArrayList<>();
            if (!result.get(1).isEmpty()) {
                errorMessages.add("Employees do not exist: " + result.get(1));
            }
            if(!result.get(2).isEmpty()) {
                errorMessages.add("Employees have already have 3 same certificate: " + result.get(2));
            }
            if(!result.get(3).isEmpty()) {
                errorMessages.add(
                    "Employees have already the certificate issued by the province: " + result.get(3)
                );
            }
            dto.setEmployeeIds(result.get(0));
            return new ComplexResponse<>(dto, 200, errorMessages);
        } catch (IllegalArgumentException e) {
            return new ComplexResponse<>(
                null, 400, Collections.singletonList(e.getMessage())
            );
        }
    }
}
