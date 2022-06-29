package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Certificate;
import com.globits.da.dto.CertificateDto;
import com.globits.da.dto.search.CertificateSearchDto;
import com.globits.da.repository.CertificateRepository;
import com.globits.da.service.CertificateService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.PageUtil;
import com.globits.da.utils.Response;
import com.globits.da.validatator.CertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateServiceImpl extends GenericServiceImpl<Certificate, UUID> implements CertificateService {

    @Autowired
    CertificateRepository repos;

    @Autowired
    CertificateValidator validator;

    @Override
    public Response<List<CertificateDto>> getAll() {
        return new Response<>(
                repos.getAll(),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<CertificateDto> save(CertificateDto dto) {
        if(validator.isDuplicateCode(dto.getCode(), null)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }
        Certificate entity = new Certificate();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity = repos.saveAndFlush(entity);
        return new Response<>(
                new CertificateDto(entity),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<CertificateDto> update(UUID id, CertificateDto dto) {
        if (id == null) {
            return new Response<>(
                    null,
                    ErrorMessage.ID_IS_NULL.getCode(),
                    ErrorMessage.ID_IS_NULL.getMessage()
            );
        }
        if(!repos.existsById(id)) {
            return new Response<>(
                    null,
                    ErrorMessage.CERTIFICATE_NOT_EXIST.getCode(),
                    ErrorMessage.CERTIFICATE_NOT_EXIST.getMessage()
            );
        }
        Certificate entity = repos.getById(id);
        if(validator.isDuplicateCode(dto.getCode(), entity)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity = repos.saveAndFlush(entity);
        return new Response<>(
                new CertificateDto(entity),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Boolean> deleteById(UUID id) {
        if (id == null) {
            return new Response<>(
                    false,
                    ErrorMessage.ID_IS_NULL.getCode(),
                    ErrorMessage.ID_IS_NULL.getMessage()
            );
        }
        if(!repos.existsById(id)) {
            return new Response<>(
                    false,
                    ErrorMessage.CERTIFICATE_NOT_EXIST.getCode(),
                    ErrorMessage.CERTIFICATE_NOT_EXIST.getMessage()
            );
        }
        repos.deleteById(id);
        return  new Response<>(
                true,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<CertificateDto>> getPage(Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(
                PageUtil.validatePageIndex(pageIndex),
                PageUtil.validatePageSize(pageSize)
        );
        return new Response<>(
                repos.getPage(pageable),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<CertificateDto>> search(CertificateSearchDto dto) {
        String sql = "select new com.globits.da.dto.CertificateDto(entity) from Certificate as entity where (1=1)";
        String countSql = "select count(entity.id) from Certificate as entity where (1=1)";
        String whereClause = "";
        String orderByClause = "";

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause = " and entity.name like :keyword";
        }

        if(dto.isOrderBy()) {
            orderByClause = " order by entity.name asc";
        }

        sql += whereClause + orderByClause;
        countSql += whereClause;

        Query query = manager.createQuery(sql);
        Query countQuery = manager.createQuery(countSql);

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
            countQuery.setParameter("keyword", "%" + dto.getKeyword() + "%");
        }

        Integer pageIndex = PageUtil.validatePageIndex(dto.getPageIndex());
        Integer pageSize = PageUtil.validatePageSize(dto.getPageSize());

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<CertificateDto> certificateList = query.getResultList();
        long count = (long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new Response<>(
                new PageImpl<>(certificateList, pageable, count),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }
}
