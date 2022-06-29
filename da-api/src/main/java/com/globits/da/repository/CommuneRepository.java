package com.globits.da.repository;

import com.globits.da.domain.Commune;
import com.globits.da.dto.CommuneDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommuneRepository extends JpaRepository<Commune, UUID> {
    @Query("select new com.globits.da.dto.CommuneDto(entity) from Commune as entity")
    List<CommuneDto> getAll();

    @Query("select new com.globits.da.dto.CommuneDto(entity) from Commune as entity")
    Page<CommuneDto> getPage(Pageable pageable);

    @Query("select count(entity.id) from Commune entity where entity.code =?1")
    Long countByCode(String code);

    Commune getById(UUID id);
}
