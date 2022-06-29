package com.globits.da.rest;

import com.globits.da.dto.EmployeeDto;
import com.globits.da.dto.search.EmployeeSearchDto;
import com.globits.da.service.EmployeeService;
import com.globits.da.utils.ComplexResponse;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class RestEmployeeController {

    Logger logger = LoggerFactory.getLogger(RestEmployeeController.class);

    @Autowired
    EmployeeService employeeService;

    @GetMapping
    public Response<List<EmployeeDto>> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{pageIndex:[\\d]+}/{pageSize:[\\d]+}")
    public Response<Page<EmployeeDto>> getPage(
            @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return employeeService.getPage(pageIndex, pageSize);
    }

    @PostMapping("/search")
    public Response<Page<EmployeeDto>> search(@RequestBody EmployeeSearchDto dto) {
        return employeeService.search(dto);
    }

    @PostMapping
    public Response<EmployeeDto> create(@RequestBody EmployeeDto dto) {
        return employeeService.save(dto);
    }

    @PutMapping("/{id}")
    public Response<EmployeeDto> update(
            @RequestBody EmployeeDto dto, @PathVariable UUID id
    ) {
        return employeeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> delete(@PathVariable UUID id) {
        return employeeService.deleteById(id);
    }

    @GetMapping(value = "/excel")
    public Response<File> exportExcel() {
        return employeeService.exportExcel();
    }

//    @PostMapping("/excel")
//    public Response<Boolean> importExcel(@RequestParam("file") MultipartFile file) {
//
//    }
}
