package com.globits.da.repository;

import com.globits.da.domain.Province;
import com.globits.da.dto.ProvinceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProvinceRepository extends JpaRepository<Province, UUID> {
    @Query("select new com.globits.da.dto.ProvinceDto(entity) from Province entity")
    List<ProvinceDto> getAll();

    @Query("select new com.globits.da.dto.ProvinceDto(entity) from Province entity")
    Page<ProvinceDto> getPage(Pageable pageable);

    @Query("select entity from Province entity where entity.name like ?1 or entity.code like ?1")
    List<Province> getByNameOrCode(String keyword);

    @Query(value = "select count(entity.id) from Province entity where entity.code =?1")
    Long countByCode(String code);

    Province getById(UUID id);
}
