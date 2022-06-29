package com.globits.da.service;

import com.globits.core.service.GenericService;
import com.globits.da.domain.Commune;
import com.globits.da.dto.CommuneDto;
import com.globits.da.dto.search.CommuneSearchDto;
import com.globits.da.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CommuneService extends GenericService<Commune, UUID> {
    Response<List<CommuneDto>> getAll();
    Response<CommuneDto> save(CommuneDto dto);
    Response<CommuneDto> update(UUID id, CommuneDto dto);
    Response<Boolean> deleteById(UUID id);
    Response<Page<CommuneDto>> getPage(int pageIndex, int pageSise);
    Response<Page<CommuneDto>> search(CommuneSearchDto dto);
    Response<List<CommuneDto>> searchByDistrictId(UUID districtId);
}
