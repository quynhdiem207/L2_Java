package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.Employee;
import com.globits.da.dto.EmployeeDto;
import com.globits.da.dto.search.EmployeeSearchDto;
import com.globits.da.utils.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService extends GenericService<Employee, UUID> {
    Response<List<EmployeeDto>> getAll();
    Response<Page<EmployeeDto>> getPage(int pageIndex, int pageSize);
    Response<Page<EmployeeDto>> search(EmployeeSearchDto dto);
    Response<EmployeeDto> save(EmployeeDto dto);
    Response<EmployeeDto> update(UUID id, EmployeeDto dto);
    Response<Boolean> deleteById(UUID id);
    Response<File> exportExcel();
    Response<Boolean> importExcel(InputStream inputStream, String fileName);
}
