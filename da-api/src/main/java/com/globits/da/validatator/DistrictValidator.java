package com.globits.da.validatator;

import com.globits.da.domain.District;
import com.globits.da.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DistrictValidator {

    @Autowired
    DistrictRepository repos;

    public boolean isDuplicateCode(String code, District district) {
        if(code == null || !StringUtils.hasText(code)) {
            return false;
        }
        if (district != null && code.equals(district.getCode())) {
            return false;
        }
        if(repos.countByCode(code) == 0) {
            return false;
        }
        return true;
    }
}
