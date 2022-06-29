package com.globits.da.validatator;

import com.globits.da.domain.Certificate;
import com.globits.da.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CertificateValidator {

    @Autowired
    CertificateRepository repos;

    public boolean isDuplicateCode(String code, Certificate certificate) {
        if(code == null || !StringUtils.hasText(code)) {
            return false;
        }
        if (certificate != null && code.equals(certificate.getCode())) {
            return false;
        }
        if(repos.countByCode(code) == 0) {
            return false;
        }
        return true;
    }
}
