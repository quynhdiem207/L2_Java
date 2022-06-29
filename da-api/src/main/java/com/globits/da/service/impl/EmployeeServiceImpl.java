package com.globits.da.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.da.domain.Commune;
import com.globits.da.domain.District;
import com.globits.da.domain.Employee;
import com.globits.da.domain.Province;
import com.globits.da.dto.EmployeeDto;
import com.globits.da.dto.search.EmployeeSearchDto;
import com.globits.da.repository.CommuneRepository;
import com.globits.da.repository.DistrictRepository;
import com.globits.da.repository.EmployeeRepository;
import com.globits.da.repository.ProvinceRepository;
import com.globits.da.service.EmployeeService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.ImportExportExcelUtil;
import com.globits.da.utils.PageUtil;
import com.globits.da.utils.Response;
import com.globits.da.validatator.EmployeeValidator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class EmployeeServiceImpl extends GenericServiceImpl<Employee, UUID> implements EmployeeService {

    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    EmployeeRepository repos;

    @Autowired
    ProvinceRepository provinceRepos;

    @Autowired
    DistrictRepository districtRepos;

    @Autowired
    CommuneRepository communeRepos;

    @Autowired
    EmployeeValidator validator;

    @Override
    public Response<List<EmployeeDto>> getAll() {
        return new Response<>(
                repos.getAll(), ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<EmployeeDto>> getPage(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(
                PageUtil.validatePageIndex(pageIndex),
                PageUtil.validatePageSize(pageSize)
        );
        return new Response<>(
                repos.getPage(pageable), ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    @Override
    public Response<Page<EmployeeDto>> search(EmployeeSearchDto dto) {
        String sqlCount = "select count(entity.id) from  Employee as entity where (1=1) ";
        String sql = "select new com.globits.da.dto.EmployeeDto(entity) from  Employee as entity where (1=1) ";

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

        Query q = manager.createQuery(sql, EmployeeDto.class);
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
        List<EmployeeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new Response<>(
                new PageImpl<EmployeeDto>(entities, pageable, count),
                ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
        );
    }

    ErrorMessage validate(EmployeeDto dto, Employee employee) {
        // code
        if (!validator.isRequired(dto.getCode())) {
            return ErrorMessage.CODE_IS_NULL;
        }
        if(validator.hasSpace(dto.getCode())) {
            return ErrorMessage.CODE_CONTAIN_SPACE;
        }
        if (!validator.isCodeLengthValid(dto.getCode())) {
            return ErrorMessage.CODE_LENGTH_INVALID;
        }
        if (!validator.isCodeDuplicate(dto.getCode(), employee)) {
            return ErrorMessage.CODE_IS_EXIST;
        }

        // name
        if (!validator.isRequired(dto.getName())) {
            return ErrorMessage.NAME_IS_NULL;
        }

        // email
        if (!validator.isRequired(dto.getEmail())) {
            return ErrorMessage.EMAIL_IS_NULL;
        }
        if (!validator.isEmailValid(dto.getEmail())) {
            return ErrorMessage.EMAIL_IS_INVALID;
        }

        // phone
        if (!validator.isRequired(dto.getPhone())) {
            return ErrorMessage.PHONE_IS_NULL;
        }
        if (!validator.isPhoneValid(dto.getPhone())) {
            return ErrorMessage.PHONE_IS_INVALID;
        }

        // age
        if (!validator.isAgeValid(dto.getAge())) {
            return ErrorMessage.AGE_IS_INVALID;
        }

        // address
        if(dto.getProvinceId() == null) {
            return ErrorMessage.PROVINCE_IS_NULL;
        }
        if(dto.getDistrictId() == null) {
            return ErrorMessage.DISTRICT_IS_NULL;
        }
        if (dto.getCommuneId() == null) {
            return ErrorMessage.COMMUNE_IS_NULL;
        }

        return ErrorMessage.SUCCESS;
    }

    ErrorMessage isValidAddress() {

    }

    void setInfo(
            EmployeeDto dto, Employee entity,
            Province province, District district, Commune commune
    ) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAge(dto.getAge());
        entity.setCommune(commune);
        entity.setDistrict(district);
        entity.setProvince(province);
    }

    @Override
    public Response<EmployeeDto> save(EmployeeDto dto) {
        ErrorMessage errorMessage = validate(dto, null);
        if (errorMessage != ErrorMessage.SUCCESS) {
            return new Response<>(
                null, errorMessage.getCode(), errorMessage.getMessage()
            );
        }

        Commune commune = communeRepos.getById(dto.getCommuneId());
        District district = districtRepos.getById(dto.getDistrictId());
        Province province = provinceRepos.getById(dto.getProvinceId());

        if (commune == null) {
            errorMessage = ErrorMessage.COMMUNE_NOT_EXIST;
        }
        if (district == null) {
            errorMessage = ErrorMessage.DISTRICT_NOT_EXIST;
        }
        if (province == null) {
            errorMessage = ErrorMessage.PROVINCE_NOT_EXIST;
        }
        if(!commune.getDistrict().getId().equals(dto.getDistrictId())) {
            errorMessage = ErrorMessage.COMMUNE_IS_INVALID;
        }
        if(!district.getProvince().getId().equals(dto.getProvinceId())) {
            errorMessage = ErrorMessage.DISTRICT_IS_INVALID;
        }

        Employee entity = saveOrUpdate(dto, null);
        entity = repos.saveAndFlush(entity);
        return new Response<>(
            new EmployeeDto(entity), errorMessage.getCode(), errorMessage.getMessage()
        );
    }

    ErrorMessage checkExist(UUID id) {
        if (id == null) {
            return ErrorMessage.ID_IS_NULL;
        }
        if(!repos.existsById(id)) {
            return ErrorMessage.EMPLOYEE_NOT_EXIST;
        }
        return ErrorMessage.SUCCESS;
    }

    @Override
    public Response<EmployeeDto> update(UUID id, EmployeeDto dto) {
        ErrorMessage errorMessage = checkExist(id);
        if (errorMessage != ErrorMessage.SUCCESS) {
            return new Response<>(null, errorMessage.getCode(), errorMessage.getMessage());
        }

        Employee entity = repos.getById(id);

        entity = saveOrUpdate(dto, entity);
        entity = repos.save(entity);

        return new Response<>(new EmployeeDto(entity), errorMessage.getCode(), errorMessage.getMessage());
    }

    @Override
    public Response<Boolean> deleteById(UUID id) {
        ErrorMessage errorMessage = checkExist(id);
        if (errorMessage != ErrorMessage.SUCCESS) {
            return new Response<>(false, errorMessage.getCode(), errorMessage.getMessage());
        }
        return new Response<>(true, errorMessage.getCode(), errorMessage.getMessage());
    }

    @Override
    public Response<File> exportExcel() {
        List<EmployeeDto> employeeList = repos.getAll();
        try {
            return new Response<>(
                ImportExportExcelUtil.exportEmployeeList(employeeList),
                ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
            );
        } catch (IOException e) {
            return new Response<>(
                null, ErrorMessage.SERVER_ERROR.getCode(), ErrorMessage.SERVER_ERROR.getMessage()
            );
        }
    }

    @Override
    public Response<Boolean> importExcel(InputStream inputStream, String fileName) {
        try {
            List<EmployeeDto> employeeDtos = ImportExportExcelUtil.importEmployeeList(
                    inputStream, fileName
            );

            List<Employee> employees = new ArrayList<>();
            int index = 0;
            ErrorMessage errorMessage;
            Employee entity;

            for (EmployeeDto employeeDto : employeeDtos) {
                index++;

                errorMessage = validate(employeeDto, null);
                if (errorMessage != ErrorMessage.SUCCESS) {
                    repos.saveAll(employees);
                    return new Response<>(
                        false, errorMessage.getCode(), errorMessage.getMessage()
                    );
                }

                entity = saveOrUpdate(employeeDto, null);
                employees.add(entity);
            }

            repos.saveAll(employees);

            return new Response<>(
                true, ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage()
            );
        } catch (IOException e) {
            return new Response<>(
                false, ErrorMessage.SERVER_ERROR.getCode(), ErrorMessage.SERVER_ERROR.getMessage()
            );
        }
    }
}
