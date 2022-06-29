package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.EmployeeCertificate;
import com.globits.da.dto.EmployeeCertificateDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeCertificateService extends GenericService<EmployeeCertificate, UUID> {
    List<List<UUID>> issueCertificate(EmployeeCertificateDto dto);
}
