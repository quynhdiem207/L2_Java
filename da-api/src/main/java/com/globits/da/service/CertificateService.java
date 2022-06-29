package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.Certificate;
import com.globits.da.dto.CertificateDto;
import com.globits.da.dto.search.CertificateSearchDto;
import com.globits.da.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CertificateService extends GenericService<Certificate, UUID> {
    Response<List<CertificateDto>> getAll();
    Response<CertificateDto> save(CertificateDto dto);
    Response<CertificateDto> update(UUID id, CertificateDto dto);
    Response<Boolean> deleteById(UUID id);
    Response<Page<CertificateDto>> getPage(Integer pageIndex, Integer pageSize);
    Response<Page<CertificateDto>> search(CertificateSearchDto dto);
}
