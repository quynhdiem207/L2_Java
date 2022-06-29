package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Certificate;
import com.globits.da.domain.EmployeeCertificate;
import com.globits.da.domain.Province;
import com.globits.da.dto.EmployeeCertificateDto;
import com.globits.da.repository.CertificateRepository;
import com.globits.da.repository.EmployeeCertificateRepository;
import com.globits.da.repository.EmployeeRepository;
import com.globits.da.repository.ProvinceRepository;
import com.globits.da.service.EmployeeCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeCertificateServiceImpl extends GenericServiceImpl<EmployeeCertificate, UUID>
implements EmployeeCertificateService {

    @Autowired
    ProvinceRepository provinceRepos;

    @Autowired
    CertificateRepository certificateRepos;

    @Autowired
    EmployeeRepository employeeRepos;

    @Autowired
    EmployeeCertificateRepository repos;

    @Override
    public List<List<UUID>> issueCertificate(EmployeeCertificateDto dto) {
        if(dto.getProvinceId() == null || !provinceRepos.existsById(dto.getProvinceId())) {
            throw new IllegalArgumentException("Not exist the province with this ID");
        }
        if(dto.getCertificateId() == null || !certificateRepos.existsById(dto.getCertificateId())) {
            throw new IllegalArgumentException("Not exist the certificate with this ID");
        }
        if(
            dto.getEffectiveDate() == null
            || dto.getExpirationDate() == null
            || dto.getExpirationDate().isBefore(dto.getEffectiveDate())
        ) {
            throw new IllegalArgumentException("The effective and expiration date are invalid");
        }

        Province province = provinceRepos.getById(dto.getProvinceId());
        Certificate certificate = certificateRepos.getById(dto.getCertificateId());

        List<EmployeeCertificate> certificates = new ArrayList<>();
        List<UUID> ok = new ArrayList<>();
        List<UUID> notExistIds = new ArrayList<>();
        List<UUID> moreCertificationIds = new ArrayList<>();
        List<UUID> hadCertificationIds = new ArrayList<>();

        for(UUID employeeId : dto.getEmployeeIds()) {
            if(employeeId == null || !employeeRepos.existsById(employeeId)) {
                notExistIds.add(employeeId);
                continue;
            }

            List<EmployeeCertificate> effectiveCertificates = repos.getCertificateByEmployee(
                    employeeId, dto.getCertificateId()
            );

            if (effectiveCertificates.size() > 2) {
                moreCertificationIds.add(employeeId);
                continue;
            }

            boolean isDuplicate = effectiveCertificates.stream()
                    .anyMatch(effective ->  effective.getProvince().getId().equals(dto.getProvinceId()));

            if (isDuplicate) {
                hadCertificationIds.add(employeeId);
                continue;
            }

            EmployeeCertificate employeeCertificate = new EmployeeCertificate();

            employeeCertificate.setProvince(province);
            employeeCertificate.setCertificate(certificate);
            employeeCertificate.setEffectiveDate(dto.getEffectiveDate());
            employeeCertificate.setExpirationDate(dto.getExpirationDate());
            employeeCertificate.setEmployee(employeeRepos.getById(employeeId));

            certificates.add(employeeCertificate);
            ok.add(employeeId);
        }

        repos.saveAll(certificates);

        List<List<UUID>> result = Arrays.asList(
            ok, notExistIds, moreCertificationIds, hadCertificationIds
        );
        return result;
    }
}
