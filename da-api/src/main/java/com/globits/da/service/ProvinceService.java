package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.Province;
import com.globits.da.dto.DistrictDto;
import com.globits.da.dto.ProvinceDto;
import com.globits.da.dto.search.ProvinceSearchDto;
import com.globits.da.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProvinceService extends GenericService<Province, UUID> {
    Response<List<ProvinceDto>> getAll();
    Response<ProvinceDto> save(ProvinceDto dto);
    Response<ProvinceDto> update(UUID id, ProvinceDto dto);
    Response<Boolean> deleteById(UUID id);
    Response<Page<ProvinceDto>> getPage(int pageIndex, int PageSize);
    Response<Page<ProvinceDto>> search(ProvinceSearchDto dto);
    ProvinceDto getById(UUID id);
}
