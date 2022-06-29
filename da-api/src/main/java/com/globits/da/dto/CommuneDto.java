package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.Commune;

import java.util.UUID;

public class CommuneDto extends BaseObjectDto {
    private String code;
    private String name;
    private UUID districtId;

    public CommuneDto() {}

    public CommuneDto(Commune entity) {
        if (entity != null) {
            this.setId(entity.getId());
            this.code = entity.getCode();
            this.name = entity.getName();
            this.districtId = entity.getDistrict().getId();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }

    @Override
    public String toString() {
        return "{code = " + code + ", name = " + name + '}';
    }
}
