package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Commune;
import com.globits.da.domain.District;
import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.search.CommuneSearchDto;
import com.globits.da.repository.CommuneRepository;
import com.globits.da.repository.DistrictRepository;
import com.globits.da.service.CommuneService;
import com.globits.da.service.DistrictService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.PageUtil;
import com.globits.da.utils.Response;
import com.globits.da.validatator.CommuneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommuneServiceImpl extends GenericServiceImpl<Commune, UUID> implements CommuneService {

    @Autowired
    CommuneRepository repos;

    @Autowired
    DistrictRepository districtRepos;

    @Autowired
    DistrictService districtService;

    @Autowired
    CommuneValidator validation;

    @Override
    public Response<List<CommuneDto>> getAll() {
        List<CommuneDto> communeDtos = repos.getAll();
        return new Response<>(
                communeDtos,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<CommuneDto> save(CommuneDto dto) {
        if (dto.getDistrictId() == null) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_IS_NULL.getCode(),
                    ErrorMessage.DISTRICT_IS_NULL.getMessage()
            );
        }
        if(!districtRepos.existsById(dto.getDistrictId())) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_NOT_EXIST.getCode(),
                    ErrorMessage.DISTRICT_NOT_EXIST.getMessage()
            );
        }

        if(validation.isDuplicateCode(dto.getCode(), null)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }

        District district = districtRepos.getById(dto.getDistrictId());

        Commune entity = new Commune();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDistrict(district);
        entity = repos.save(entity);

        return new Response<>(
                new CommuneDto(entity),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<CommuneDto> update(UUID id, CommuneDto dto) {
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
                    ErrorMessage.COMMUNE_NOT_EXIST.getCode(),
                    ErrorMessage.COMMUNE_NOT_EXIST.getMessage()
            );
        }

        if (dto.getDistrictId() == null) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_IS_NULL.getCode(),
                    ErrorMessage.DISTRICT_IS_NULL.getMessage()
            );
        }

        if(!districtRepos.existsById(dto.getDistrictId())) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_NOT_EXIST.getCode(),
                    ErrorMessage.DISTRICT_NOT_EXIST.getMessage()
            );
        }

        Commune entity = repos.getById(id);

        if(validation.isDuplicateCode(dto.getCode(), entity)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }

        District district = districtRepos.getById(dto.getDistrictId());

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDistrict(district);
        entity = repos.save(entity);

        return new Response<>(
                new CommuneDto(entity),
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
                    ErrorMessage.COMMUNE_NOT_EXIST.getCode(),
                    ErrorMessage.COMMUNE_NOT_EXIST.getMessage()
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
    public Response<Page<CommuneDto>> getPage(int pageIndex, int pageSize) {
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
    public Response<Page<CommuneDto>> search(CommuneSearchDto dto) {
        String sql = "select new com.globits.da.dto.CommuneDto(entity) from Commune as entity where (1=1)";
        String countSql = "select count(entity.id) from Commune as entity where (1=1)";
        String whereClause = "";
        String orderByClause = "";
        List<UUID> districtIds = null;

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " and entity.name like :keyword or entity.code like :keyword";
            districtIds = districtRepos.getByNameOrCode("%" +dto.getKeyword() + "%")
                    .stream().map(district -> district.getId()).collect(Collectors.toList());
            if (!districtIds.isEmpty()) {
                ListIterator iterator = districtIds.listIterator();
                while (iterator.hasNext()) {
                    iterator.next();
                    whereClause += " or entity.district.id = :districtId" + (iterator.nextIndex() - 1);
                }
            }
        }

        if(dto.getOrderBy() != null && StringUtils.hasText(dto.getOrderBy())) {
            if(dto.getOrderBy().equalsIgnoreCase("code")) {
                orderByClause = " order by entity.code asc";
            } else if(dto.getOrderBy().equalsIgnoreCase("name")) {
                orderByClause = " order by entity.name asc";
            } else if(dto.getOrderBy().equalsIgnoreCase("district")) {
                orderByClause = " order by entity.district.id asc";
            }
        }

        sql += whereClause + orderByClause;
        countSql += whereClause;

        Query query = manager.createQuery(sql);
        Query countQuery = manager.createQuery(countSql);

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
            countQuery.setParameter("keyword", "%" + dto.getKeyword() + "%");
            if (!districtIds.isEmpty()) {
                ListIterator iterator = districtIds.listIterator();
                String param;
                while (iterator.hasNext()) {
                    UUID id = (UUID) iterator.next();
                    param = "districtId" +  (iterator.nextIndex() - 1);
                    query.setParameter(param, id);
                    countQuery.setParameter(param, id);
                }
            }
        }

        Integer pageIndex = PageUtil.validatePageIndex(dto.getPageIndex());
        Integer pageSize = PageUtil.validatePageSize(dto.getPageSize());

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<CommuneDto> communeList = query.getResultList();
        long count = (long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new Response<>(
                new PageImpl<>(communeList, pageable, count),
                ErrorMessage.CODE_IS_EXIST.getCode(),
                ErrorMessage.CODE_IS_EXIST.getMessage()
        );
    }

    public Response<List<CommuneDto>> searchByDistrictId(UUID districtId) {
        District district = districtRepos.getById(districtId);
        List<Commune> communes = district.getCommunes();
        List<CommuneDto> communeDtos = communes.stream()
                .map(commune -> new CommuneDto(commune))
                .collect(Collectors.toList());
        return new Response<>(
                communeDtos,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }
}
