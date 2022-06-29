package com.globits.da.rest;

import com.globits.da.dto.DistrictDto;
import com.globits.da.dto.search.DistrictSearchDto;
import com.globits.da.service.DistrictService;
import com.globits.da.utils.ComplexResponse;
import com.globits.da.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/districts")
public class RestDistrictController {

    @Autowired
    DistrictService districtService;

    @GetMapping
    public Response<List<DistrictDto>> getAll() {
        return districtService.getAll();
    }

    @PostMapping
    public Response<DistrictDto> create(@RequestBody DistrictDto dto) {
        return districtService.save(dto);
    }

    @PutMapping("/{id}")
    public Response<DistrictDto> update(@PathVariable UUID id, @RequestBody DistrictDto dto) {
        return districtService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> delete(@PathVariable UUID id) {
        return districtService.deleteById(id);
    }

    @GetMapping("/{pageIndex:[\\d]+}/{pageSize:[\\d]+}")
    public Response<Page<DistrictDto>> getPage(
            @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return districtService.getPage(pageIndex, pageSize);
    }

    @PostMapping("/search")
    public Response<Page<DistrictDto>> search(@RequestBody DistrictSearchDto dto) {
        return districtService.search(dto);
    }

    @GetMapping("/search")
    public Response<List<DistrictDto>> searchByProvinceId(@RequestParam UUID provinceId) {
        return districtService.searchByProvinceId(provinceId);
    }
}
