package com.globits.da.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.da.domain.Employee;

import java.util.UUID;

public class EmployeeDto extends BaseObjectDto {
    private String code;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private UUID provinceId;
    private UUID districtId;
    private UUID communeId;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public UUID getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(UUID provinceId) {
        this.provinceId = provinceId;
    }

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }

    public UUID getCommuneId() {
        return communeId;
    }

    public void setCommuneId(UUID communeId) {
        this.communeId = communeId;
    }

    public EmployeeDto() {}

    public EmployeeDto(Employee entity) {
        if (entity != null) {
            this.setId(entity.getId());
            this.code = entity.getCode();
            this.name = entity.getName();
            this.email = entity.getEmail();
            this.phone = entity.getPhone();
            this.age = entity.getAge();
            this.provinceId = entity.getProvince().getId();
            this.districtId = entity.getDistrict().getId();
            this.communeId = entity.getCommune().getId();
        }
    }
}
