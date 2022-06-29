package com.globits.da.validatator;

import com.globits.da.domain.Province;
import com.globits.da.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProvinceValidator {

    @Autowired
    ProvinceRepository repos;

    public boolean isDuplicateCode(String code, Province province) {
        if(code == null || !StringUtils.hasText(code)) {
            return false;
        }
        if (province != null && code.equals(province.getCode())) {
            return false;
        }
        if(repos.countByCode(code) == 0) {
            return false;
        }
        return true;
    }
}
