package com.globits.da.repository;

import com.globits.da.domain.District;
import com.globits.da.dto.DistrictDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {
    @Query("select new com.globits.da.dto.DistrictDto(entity) from District entity")
    List<DistrictDto> getAll();

    @Query("select new com.globits.da.dto.DistrictDto(entity) from District entity")
    Page<DistrictDto> getPage(Pageable pageable);

    @Query("select entity from District entity where entity.name like ?1 or entity.code like ?1")
    List<District> getByNameOrCode(String keyword);

    @Query("select count(entity.id) from District entity where entity.code =?1")
    Long countByCode(String code);

    District getById(UUID id);
}
