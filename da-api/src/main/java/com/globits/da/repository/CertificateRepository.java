package com.globits.da.repository;

import com.globits.da.domain.Certificate;
import com.globits.da.dto.CertificateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    @Query("select new com.globits.da.dto.CertificateDto(entity) from Certificate as entity")
    List<CertificateDto> getAll();

    @Query("select new com.globits.da.dto.CertificateDto(entity) from Certificate as entity")
    Page<CertificateDto> getPage(Pageable pageable);
    
    @Query("select count(entity.id) from Certificate entity where entity.code = ?1")
    Long countByCode(String code);

    Certificate getById(UUID id);
}
