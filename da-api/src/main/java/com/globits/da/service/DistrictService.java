package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.District;
import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.DistrictDto;
import com.globits.da.dto.search.DistrictSearchDto;
import com.globits.da.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DistrictService extends GenericService<District, UUID> {
    Response<List<DistrictDto>> getAll();
    Response<DistrictDto> save(DistrictDto dto);
    Response<DistrictDto> update(UUID id, DistrictDto dto);
    Response<Boolean> deleteById(UUID id);
    Response<Page<DistrictDto>> getPage(int pageIndex, int pageSize);
    Response<Page<DistrictDto>> search(DistrictSearchDto dto);
    Response<List<DistrictDto>> searchByProvinceId(UUID provinceId);
}
