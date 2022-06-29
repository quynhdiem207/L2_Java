package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Commune;
import com.globits.da.domain.District;
import com.globits.da.domain.Province;
import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.DistrictDto;
import com.globits.da.dto.search.DistrictSearchDto;
import com.globits.da.repository.CommuneRepository;
import com.globits.da.repository.DistrictRepository;
import com.globits.da.repository.ProvinceRepository;
import com.globits.da.service.DistrictService;
import com.globits.da.service.ProvinceService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.PageUtil;
import com.globits.da.utils.Response;
import com.globits.da.validatator.CommuneValidator;
import com.globits.da.validatator.DistrictValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DistrictServiceImpl extends GenericServiceImpl<District, UUID> implements DistrictService {

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    ProvinceRepository provinceRepos;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    CommuneRepository communeRepos;

    @Autowired
    DistrictValidator validator;

    @Autowired
    CommuneValidator communeValidator;

    @Override
    public Response<List<DistrictDto>> getAll() {
        return new Response<>(
                districtRepository.getAll(),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    boolean isDuplicateCommuneCode(String code, Commune commune, List<CommuneDto> communeDtos, int index) {
        if(communeValidator.isDuplicateCode(code, commune)) {
            return true;
        }
        if (index != 1) {
            return communeDtos.subList(0, index - 1).stream()
                    .anyMatch(dtoCommune -> code.equalsIgnoreCase(dtoCommune.getCode()));
        }
        return false;
    }

    @Override
    public Response<DistrictDto> save(DistrictDto dto) {
        if (dto.getProvinceId() == null) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_IS_NULL.getCode(),
                    ErrorMessage.PROVINCE_IS_NULL.getMessage()
            );
        }
        if(!provinceRepos.existsById(dto.getProvinceId())) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_NOT_EXIST.getCode(),
                    ErrorMessage.PROVINCE_NOT_EXIST.getMessage()
            );
        }
        Province province = provinceRepos.getById(dto.getProvinceId());
        if(validator.isDuplicateCode(dto.getCode(), null)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }
        District entity = new District(dto.getCode(), dto.getName(), province);
        if(dto.getCommunes() != null && !dto.getCommunes().isEmpty()) {
            List<Commune> communes = new ArrayList<>();
            int index = 0;
            for (CommuneDto communeDto : dto.getCommunes()) {
                index++;
                if(isDuplicateCommuneCode(communeDto.getCode(), null, dto.getCommunes(), index)) {
                    entity.setCommunes(communes);
                    entity = districtRepository.saveAndFlush(entity);
                    return new Response<>(
                            new DistrictDto(entity),
                            ErrorMessage.CODE_IS_EXIST.getCode(),
                            ErrorMessage.CODE_IS_EXIST.getMessage() +
                                    ": Commune " + index + " (" + communeDto + ")"
                    );
                }
                Commune commune = new Commune();
                commune.setCode(communeDto.getCode());
                commune.setName(communeDto.getName());
                commune.setDistrict(entity);
                communes.add(commune);
            }
            entity.setCommunes(communes);
        }

        entity = districtRepository.saveAndFlush(entity);
        return new Response<>(
            new DistrictDto(entity),
            ErrorMessage.SUCCESS.getCode(),
            ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<DistrictDto> update(UUID id, DistrictDto dto) {
        if (id == null) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_IS_NULL.getCode(),
                    ErrorMessage.DISTRICT_IS_NULL.getMessage()
            );
        }
        if(!districtRepository.existsById(id)) {
            return new Response<>(
                    null,
                    ErrorMessage.DISTRICT_NOT_EXIST.getCode(),
                    ErrorMessage.DISTRICT_NOT_EXIST.getMessage()
            );
        }

        if (dto.getProvinceId() == null) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_IS_NULL.getCode(),
                    ErrorMessage.PROVINCE_IS_NULL.getMessage()
            );
        }
        if(!provinceRepos.existsById(dto.getProvinceId())) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_NOT_EXIST.getCode(),
                    ErrorMessage.PROVINCE_NOT_EXIST.getMessage()
            );
        }

        District entity = districtRepository.getById(id);

        if(validator.isDuplicateCode(dto.getCode(), entity)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }

        Province province = provinceRepos.getById(dto.getProvinceId());

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setProvince(province);

        if(dto.getCommunes() != null && !dto.getCommunes().isEmpty()) {
            int indexLoop = 0;
            for (CommuneDto communeDto : dto.getCommunes()) {
                indexLoop++;

                if(!communeRepos.existsById(communeDto.getId())) {
                    districtRepository.saveAndFlush(entity);
                    return new Response<>(
                            new DistrictDto(entity), ErrorMessage.COMMUNE_NOT_EXIST.getCode(),
                            ErrorMessage.COMMUNE_NOT_EXIST.getMessage() + ": Commune " +
                                    indexLoop + " (" + communeDto.getId() + ")"
                    );
                }

                Commune commune = communeRepos.getById(communeDto.getId());

                // Check the commune is not in the district
                if (!commune.getDistrict().getId().equals(id)) {
                    districtRepository.saveAndFlush(entity);
                    return new Response<>(
                            new DistrictDto(entity),
                            ErrorMessage.COMMUNE_IS_INVALID.getCode(),
                            ErrorMessage.COMMUNE_IS_INVALID.getMessage() + ": Commune " + indexLoop +
                                    " (" + communeDto.getId() + ")"
                    );
                }

                // Check commune code is duplicate
                boolean isDuplicate = isDuplicateCommuneCode(
                    communeDto.getCode(), commune, dto.getCommunes(), indexLoop
                );
                if (isDuplicate) {
                    districtRepository.saveAndFlush(entity);
                    return new Response<>(
                            new DistrictDto(entity),
                            ErrorMessage.CODE_IS_EXIST.getCode(),
                            ErrorMessage.CODE_IS_EXIST.getMessage() + ": Commune " + indexLoop +
                                    " (" + communeDto.getId() + ")"
                    );
                }
                commune.setName(communeDto.getName());
                commune.setCode(communeDto.getCode());
            }
        }

        entity = districtRepository.saveAndFlush(entity);

        return new Response<>(
                new DistrictDto(entity),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Boolean> deleteById(UUID id) {
        if (id == null) {
            return new Response<>(
                    false,
                    ErrorMessage.DISTRICT_IS_NULL.getCode(),
                    ErrorMessage.DISTRICT_IS_NULL.getMessage()
            );
        }
        if(!districtRepository.existsById(id)) {
            return new Response<>(
                    false,
                    ErrorMessage.DISTRICT_NOT_EXIST.getCode(),
                    ErrorMessage.DISTRICT_NOT_EXIST.getMessage()
            );
        }
        districtRepository.deleteById(id);
        return new Response<>(
                true,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<DistrictDto>> getPage(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(
                PageUtil.validatePageIndex(pageIndex),
                PageUtil.validatePageSize(pageSize)
        );
        return new Response<>(
                districtRepository.getPage(pageable),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<DistrictDto>> search(DistrictSearchDto dto) {
        String sql = "select new com.globits.da.dto.DistrictDto(entity) from District as entity where (1=1)";
        String countSql = "select count(entity.id) from District as entity where (1=1)";
        String whereClause = "";
        String orderByClause = "";
        List<UUID> provinceIds = null;

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " and entity.name like :keyword or entity.code like :keyword";
            provinceIds = provinceRepos.getByNameOrCode("%" + dto.getKeyword() + "%")
                    .stream().map(province -> province.getId()).collect(Collectors.toList());
            if (!provinceIds.isEmpty()) {
                ListIterator iterator = provinceIds.listIterator();
                while (iterator.hasNext()) {
                    iterator.next();
                    whereClause += " or entity.province.id = :provinceId" + (iterator.nextIndex() - 1);
                }
            }
        }

        if(dto.getOrderBy() != null && StringUtils.hasText(dto.getOrderBy())) {
            if(dto.getOrderBy().equalsIgnoreCase("name")) {
                orderByClause = " order by entity.name asc";
            } else if(dto.getOrderBy().equalsIgnoreCase("code"))  {
                orderByClause = " order by entity.code asc";
            } else if(dto.getOrderBy().equalsIgnoreCase("province")) {
                orderByClause = " order by entity.province.id asc";
            }
        }

        sql += whereClause + orderByClause;
        countSql += whereClause;

        Query query = manager.createQuery(sql);
        Query countQuery = manager.createQuery(countSql);

        if(dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
            countQuery.setParameter("keyword", "%" + dto.getKeyword() + "%");
            if (!provinceIds.isEmpty()) {
                ListIterator iterator = provinceIds.listIterator();
                String param;
                while (iterator.hasNext()) {
                    UUID id = (UUID) iterator.next();
                    param = "provinceId" +  (iterator.nextIndex() - 1);
                    query.setParameter(param, id);
                    countQuery.setParameter(param, id);
                }
            }
        }

        Integer pageIndex = PageUtil.validatePageIndex(dto.getPageIndex());
        Integer pageSize = PageUtil.validatePageSize(dto.getPageSize());

        int startPosition = pageSize * pageIndex;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<DistrictDto> entities = query.getResultList();
        long count = (long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new Response<>(
                new PageImpl<>(entities, pageable, count),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<List<DistrictDto>> searchByProvinceId(UUID provinceId) {
        Province province = provinceRepos.getById(provinceId);
        List<District> districts = province.getDistricts();
        List<DistrictDto> districtDtos = districts.stream()
                .map(district -> new DistrictDto(district))
                .collect(Collectors.toList());
        return new Response<>(
                districtDtos,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }
}
