package com.globits.da.validatator;

import com.globits.da.domain.Commune;
import com.globits.da.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommuneValidator {
    @Autowired
    CommuneRepository repos;

    public boolean isDuplicateCode(String code, Commune commune) {
        if(code == null || !StringUtils.hasText(code)) {
            return false;
        }
        if (commune != null && code.equals(commune.getCode())) {
            return false;
        }
        if(repos.countByCode(code) == 0) {
            return false;
        }
        return true;
    }
}
