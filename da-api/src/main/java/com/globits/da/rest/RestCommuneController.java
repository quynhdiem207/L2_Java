package com.globits.da.rest;

import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.search.CommuneSearchDto;
import com.globits.da.service.CommuneService;
import com.globits.da.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/communes")
public class RestCommuneController {

    @Autowired
    CommuneService communeService;

    @GetMapping
    public Response<List<CommuneDto>> getAll() {
        return communeService.getAll();
    }

    @PostMapping
    public Response<CommuneDto> create(@RequestBody CommuneDto dto) {
        return communeService.save(dto);
    }

    @PutMapping("/{id}")
    public Response<CommuneDto> update(
            @RequestBody CommuneDto dto, @PathVariable UUID id
    ) {
       return communeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> delete(@PathVariable UUID id) {
        return communeService.deleteById(id);
    }

    @GetMapping("/{pageIndex:[\\d]+}/{pageSize:[\\d]+}")
    public Response<Page<CommuneDto>> getPage(
            @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return communeService.getPage(pageIndex, pageSize);
    }

    @PostMapping("/search")
    public Response<Page<CommuneDto>> search(@RequestBody CommuneSearchDto dto) {
        return communeService.search(dto);
    }

    @GetMapping("/search")
    public Response<List<CommuneDto>> searchByDistrictId(@RequestParam UUID districtId) {
        return communeService.searchByDistrictId(districtId);
    }
}
