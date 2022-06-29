package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.EmployeeCertificate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EmployeeCertificateDto extends BaseObjectDto {
    private UUID provinceId;
    private UUID certificateId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime effectiveDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expirationDate;

    @NotEmpty(message = "List of employees must not be empty!")
    private List<UUID> employeeIds;

    public EmployeeCertificateDto() {}

    public void setProvinceId(UUID provinceId) {
        this.provinceId = provinceId;
    }

    public void setCertificateId(UUID certificateId) {
        this.certificateId = certificateId;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setEmployeeIds(List<UUID> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public UUID getProvinceId() {
        return provinceId;
    }

    public UUID getCertificateId() {
        return certificateId;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public List<UUID> getEmployeeIds() {
        return employeeIds;
    }
}
