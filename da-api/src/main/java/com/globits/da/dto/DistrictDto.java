package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.District;

import java.util.List;
import java.util.UUID;

public class DistrictDto extends BaseObjectDto {
    private String name;
    private String code;
    private UUID provinceId;
    private List<CommuneDto> communes;

    public DistrictDto() {}

    public DistrictDto(District entity) {
        if (entity != null) {
            this.setId(entity.getId());
            this.name = entity.getName();
            this.code = entity.getCode();
            this.provinceId = entity.getProvince().getId();
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

    public UUID getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(UUID provinceId) {
        this.provinceId = provinceId;
    }

    public List<CommuneDto> getCommunes() {
        return communes;
    }

    public void setCommunes(List<CommuneDto> communes) {
        this.communes = communes;
    }

    @Override
    public String toString() {
        return "{code = " + code + ", name = " + name + '}';
    }
}
