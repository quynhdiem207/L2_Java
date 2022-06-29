package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Commune;
import com.globits.da.domain.District;
import com.globits.da.domain.Province;
import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.DistrictDto;
import com.globits.da.dto.ProvinceDto;
import com.globits.da.dto.search.ProvinceSearchDto;
import com.globits.da.repository.CommuneRepository;
import com.globits.da.repository.DistrictRepository;
import com.globits.da.repository.ProvinceRepository;
import com.globits.da.service.ProvinceService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.PageUtil;
import com.globits.da.utils.Response;
import com.globits.da.validatator.CommuneValidator;
import com.globits.da.validatator.DistrictValidator;
import com.globits.da.validatator.ProvinceValidator;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProvinceServiceImpl extends GenericServiceImpl<Province, UUID> implements ProvinceService {

    @Autowired
    ProvinceRepository repos;
    
    @Autowired
    DistrictRepository districtRepos;

    @Autowired
    CommuneRepository communeRepos;
    
    @Autowired
    ProvinceValidator validator;

    @Autowired
    DistrictValidator districtValidator;

    @Autowired
    CommuneValidator communeValidator;

    @Override
    public ProvinceDto getById(UUID id) {
        return new ProvinceDto(repos.getById(id));
    }

    @Override
    public Response<List<ProvinceDto>> getAll() {
        return new Response<>(
            repos.getAll(), ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    boolean isDuplicateDistrictCode(
            String code, District district, List<DistrictDto> districtDtos, int index
    ) {
        if(districtValidator.isDuplicateCode(code, district)) {
            return true;
        }
        if (index != 1) {
            return districtDtos.subList(0, index - 1).stream()
                    .anyMatch(dtoDistrict -> code.equalsIgnoreCase(dtoDistrict.getCode()));
        }
        return false;
    }

    boolean isDuplicateCommuneCode(
            String code, Commune commune, List<DistrictDto> districtDtos, int index, int communeIndex
    ) {
        if(communeValidator.isDuplicateCode(code, commune)) {
            return true;
        }
        boolean isDuplicate = false;
        if(communeIndex != 1) {
            isDuplicate = districtDtos.get(index - 1).getCommunes().subList(0, communeIndex - 1)
                    .stream().anyMatch(x ->  code.equals(x.getCode()));
            if(isDuplicate) return true;
        }
        if (index != 1) {
            isDuplicate = districtDtos.subList(0, index - 1).stream()
                    .anyMatch(dtoDistrict -> dtoDistrict.getCommunes().stream().anyMatch(
                            dtoCommune -> code.equalsIgnoreCase(dtoCommune.getCode())
                    ));
            return isDuplicate;
        }
        return false;
    }

    @Override
    public Response<ProvinceDto> save(ProvinceDto dto) {
        if(validator.isDuplicateCode(dto.getCode(), null)) {
            return new Response<>(
                null, ErrorMessage.CODE_IS_EXIST.getCode(), ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }

        Province entity = new Province(dto.getCode(), dto.getName());

        if(dto.getDistricts() != null) {
            int index = 0, communeIndex = 0;
            List<District> districts = new ArrayList<>();
            for (DistrictDto districtDto : dto.getDistricts()) {
                index++;
                communeIndex = 0;
                boolean isDuplicate = isDuplicateDistrictCode(
                    districtDto.getCode(), null, dto.getDistricts(), index
                );
                if (isDuplicate) {
                    entity.setDistricts(districts);
                    entity = repos.saveAndFlush(entity);
                    return new Response<>(
                        new ProvinceDto(entity),
                        ErrorMessage.CODE_IS_EXIST.getCode(),
                        ErrorMessage.CODE_IS_EXIST.getMessage() +
                                ": District " + index + " " + districtDto
                    );
                }

                District district = new District();
                district.setCode(districtDto.getCode());
                district.setName(districtDto.getName());
                district.setProvince(entity);
                districts.add(district);

                if (districtDto.getCommunes() != null && !districtDto.getCommunes().isEmpty()) {
                    List<Commune> communes = new ArrayList<>();
                    for (CommuneDto communeDto : districtDto.getCommunes()) {
                        communeIndex++;
                        isDuplicate = isDuplicateCommuneCode(
                            communeDto.getCode(), null, dto.getDistricts(), index, communeIndex
                        );
                        if (isDuplicate) {
                            district.setCommunes(communes);
                            entity.setDistricts(districts);
                            entity = repos.saveAndFlush(entity);
                            return new Response<>(
                                new ProvinceDto(entity),
                                ErrorMessage.CODE_IS_EXIST.getCode(),
                                ErrorMessage.CODE_IS_EXIST.getMessage() + ": Commune " + communeIndex + " "
                                    + communeDto + " of district " + index + " " + districtDto
                            );
                        }
                        Commune commune = new Commune();
                        commune.setCode(communeDto.getCode());
                        commune.setName(communeDto.getName());
                        commune.setDistrict(district);
                        communes.add(commune);
                    }
                    district.setCommunes(communes);
                }
            }
            entity.setDistricts(districts);
        }

        entity = repos.saveAndFlush(entity);
        return new Response<>(
                new ProvinceDto(entity), ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<ProvinceDto> update(UUID id, ProvinceDto dto) {
        if (id == null) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_IS_NULL.getCode(),
                    ErrorMessage.PROVINCE_IS_NULL.getMessage()
            );
        }
        if(!repos.existsById(id)) {
            return new Response<>(
                    null,
                    ErrorMessage.PROVINCE_NOT_EXIST.getCode(),
                    ErrorMessage.PROVINCE_NOT_EXIST.getMessage()
            );
        }
        Province entity = repos.getById(id);
        if(validator.isDuplicateCode(dto.getCode(), entity)) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_EXIST.getCode(),
                    ErrorMessage.CODE_IS_EXIST.getMessage()
            );
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());

        if(dto.getDistricts() != null) {
            int index = 0, communeIndex = 0;
            for (DistrictDto districtDto : dto.getDistricts()) {
                index++;
                communeIndex = 0;

                if (districtDto.getId() == null) {
                    repos.saveAndFlush(entity);
                    return new Response<>(
                            new ProvinceDto(entity), ErrorMessage.DISTRICT_IS_NULL.getCode(),
                            ErrorMessage.DISTRICT_IS_NULL.getMessage() + ": District " +
                                    index + " (" + districtDto.getId() + ")"
                    );
                }

                if (!districtRepos.existsById(districtDto.getId())) {
                    repos.saveAndFlush(entity);
                    return new Response<>(
                            new ProvinceDto(entity), ErrorMessage.DISTRICT_NOT_EXIST.getCode(),
                            ErrorMessage.DISTRICT_NOT_EXIST.getMessage() + ": District " +
                                    index + " (" + districtDto.getId() + ")"
                    );
                }

                District district = districtRepos.getById(districtDto.getId());

                // Check the district is not in the province
                if (!district.getProvince().getId().equals(id)) {
                    repos.saveAndFlush(entity);
                    return new Response<>(
                            new ProvinceDto(entity),
                            ErrorMessage.DISTRICT_IS_INVALID.getCode(),
                            ErrorMessage.DISTRICT_IS_INVALID.getMessage() + ": District " + index +
                                    " (" + districtDto.getId() + ")"
                    );
                }

                // Check district code is duplicate
                boolean isDuplicate = isDuplicateDistrictCode(
                        districtDto.getCode(), district, dto.getDistricts(), index
                );
                if (isDuplicate) {
                    repos.saveAndFlush(entity);
                    return new Response<>(
                            new ProvinceDto(entity),
                            ErrorMessage.CODE_IS_EXIST.getCode(),
                            ErrorMessage.CODE_IS_EXIST.getMessage() + ": District " + index +
                                    " (" + districtDto.getId() + ")"
                    );
                }

                district.setName(districtDto.getName());
                district.setCode(districtDto.getCode());
                district.setProvince(entity);

                if(districtDto.getCommunes() != null && !districtDto.getCommunes().isEmpty()) {
                    for (CommuneDto communeDto : districtDto.getCommunes()) {
                        communeIndex++;

                        if(communeDto.getId() == null) {
                            repos.saveAndFlush(entity);
                            return new Response<>(
                                    new ProvinceDto(entity), ErrorMessage.COMMUNE_IS_NULL.getCode(),
                                    String.format(
                                            "%s: Commune %d (%s) of District %d (%s)",
                                            ErrorMessage.COMMUNE_IS_NULL.getMessage(),
                                            communeIndex, communeDto.getId(), index, districtDto.getId()
                                    )
                            );
                        }

                        if(!communeRepos.existsById(communeDto.getId())) {
                            repos.saveAndFlush(entity);
                            return new Response<>(
                                new ProvinceDto(entity), ErrorMessage.COMMUNE_NOT_EXIST.getCode(),
                                String.format(
                                        "%s: Commune %d (%s) of District %d (%s)",
                                        ErrorMessage.COMMUNE_NOT_EXIST.getMessage(),
                                        communeIndex, communeDto.getId(), index, districtDto.getId()
                                )
                            );
                        }

                        Commune commune = communeRepos.getById(communeDto.getId());

                        // Check the commune is not in the district
                        if (!commune.getDistrict().getId().equals(districtDto.getId())) {
                            repos.saveAndFlush(entity);
                            return new Response<>(
                                    new ProvinceDto(entity),
                                    ErrorMessage.COMMUNE_IS_INVALID.getCode(),
                                    String.format(
                                            "%s: Commune %d (%s) of District %d (%s)",
                                            ErrorMessage.COMMUNE_IS_INVALID.getMessage(),
                                            communeIndex, communeDto.getId(), index, districtDto.getId()
                                    )
                            );
                        }

                        // Check commune code is duplicate
                        isDuplicate = isDuplicateCommuneCode(
                                communeDto.getCode(), commune, dto.getDistricts(), index, communeIndex
                        );
                        if (isDuplicate) {
                            repos.saveAndFlush(entity);
                            return new Response<>(
                                    new ProvinceDto(entity),
                                    ErrorMessage.CODE_IS_EXIST.getCode(),
                                    String.format(
                                            "%s: Commune %d (%s) of District %d (%s)",
                                            ErrorMessage.CODE_IS_EXIST.getMessage(),
                                            communeIndex, communeDto.getId(), index, districtDto.getId()
                                    )
                            );
                        }

                        commune.setName(communeDto.getName());
                        commune.setCode(communeDto.getCode());
                        commune.setDistrict(district);
                    }
                }
            }
        }

        entity = repos.save(entity);
        return new Response<>(
                new ProvinceDto(entity), ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Boolean> deleteById(UUID id) {
        if (id == null) {
            return new Response<>(
                    false,
                    ErrorMessage.PROVINCE_IS_NULL.getCode(),
                    ErrorMessage.PROVINCE_IS_NULL.getMessage()
            );
        }
        if(!repos.existsById(id)) {
            return new Response<>(
                    false,
                    ErrorMessage.PROVINCE_NOT_EXIST.getCode(),
                    ErrorMessage.PROVINCE_NOT_EXIST.getMessage()
            );
        }
        repos.deleteById(id);
        return new Response<>(
                true,
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<ProvinceDto>> getPage(int pageIndex, int pageSize) {
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
    public Response<Page<ProvinceDto>> search(ProvinceSearchDto dto) {
        String sqlCount = "select count(entity.id) from  Province as entity where (1=1) ";
        String sql = "select new com.globits.da.dto.ProvinceDto(entity) from  Province as entity where (1=1) ";

        String whereClause = "";
        String orderBy = "";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :keyword OR entity.code LIKE :keyword )";
        }

        if (dto.getOrderBy() != null && StringUtils.hasText(dto.getOrderBy())) {
            if ("code".equalsIgnoreCase(dto.getOrderBy())) {
                orderBy = " ORDER BY entity.code ASC";
            } else if("name".equalsIgnoreCase(dto.getOrderBy())) {
                orderBy = " ORDER BY entity.name ASC";
            }
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ProvinceDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("keyword", '%' + dto.getKeyword() + '%');
            qCount.setParameter("keyword", '%' + dto.getKeyword() + '%');
        }

        Integer pageIndex = PageUtil.validatePageIndex(dto.getPageIndex());
        Integer pageSize = PageUtil.validatePageSize(dto.getPageSize());

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);

        List<ProvinceDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new Response<>(
                new PageImpl<ProvinceDto>(entities, pageable, count),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }
}
