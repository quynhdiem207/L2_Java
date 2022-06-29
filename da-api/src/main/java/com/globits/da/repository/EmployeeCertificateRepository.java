package com.globits.da.repository;

import com.globits.da.domain.EmployeeCertificate;
import com.globits.da.dto.EmployeeCertificateDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeCertificateRepository extends JpaRepository<EmployeeCertificate, UUID> {

    @Query(
        "select entity from EmployeeCertificate entity" +
        " where entity.certificate.id = ?2 and entity.employee.id = ?1 and entity.expirationDate > current_timestamp"
    )
    List<EmployeeCertificate> getCertificateByEmployee(
        UUID employeeId,
        UUID certificateId
    );
}
