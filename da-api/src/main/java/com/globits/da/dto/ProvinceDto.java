package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.Province;

import java.util.List;

public class ProvinceDto extends BaseObjectDto {
    private String code;
    private String name;
    private List<DistrictDto> districts;

    public ProvinceDto() {}

    public ProvinceDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ProvinceDto(Province entity) {
        if(entity != null) {
            this.setId(entity.getId());
            this.name = entity.getName();
            this.code = entity.getCode();
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

    public List<DistrictDto> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDto> districts) {
        this.districts = districts;
    }
}
