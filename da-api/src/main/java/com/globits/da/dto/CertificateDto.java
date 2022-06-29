package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.Certificate;

import javax.validation.constraints.NotEmpty;

public class CertificateDto extends BaseObjectDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private String code;

    public CertificateDto() {}

    public CertificateDto(Certificate entity) {
        if (entity != null) {
            this.setId(entity.getId());
            this.name = entity.getName();
            this.code = entity.getCode();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
