package com.globits.da.rest;

import com.globits.da.dto.ProvinceDto;
import com.globits.da.dto.search.ProvinceSearchDto;
import com.globits.da.service.ProvinceService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/provinces")
public class RestProvinceController {

    Logger logger = LoggerFactory.getLogger(RestProvinceController.class);

    @Autowired
    ProvinceService provinceService;

    @GetMapping
    public Response<List<ProvinceDto>> getAll() {
        return provinceService.getAll();
    }

    @PostMapping
    public Response<ProvinceDto> create(@RequestBody ProvinceDto dto) {
        return provinceService.save(dto);
    }

    @PutMapping("/{id}")
    public Response<ProvinceDto> update(
            @RequestBody ProvinceDto dto, @PathVariable UUID id
    ) {
        return provinceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> delete(@PathVariable UUID id) {
        return provinceService.deleteById(id);
    }

    @GetMapping("/{pageIndex:[\\d]+}/{pageSize:[\\d]+}")
    public Response<Page<ProvinceDto>> getPage(
            @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return new Response(
                provinceService.getPage(pageIndex, pageSize),
                ErrorMessage.SUCCESS.getCode(),
                ErrorMessage.SUCCESS.getMessage()
        );
    }

    @PostMapping("/search")
    public Response<Page<ProvinceDto>> search(@RequestBody ProvinceSearchDto dto) {
        return provinceService.search(dto);
    }
}
