package com.devops.qvs.repository;

import com.devops.qvs.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Optional<Institution> findByInstitutionCode(String institutionCode);
    Optional<Institution> findByName(String name);
    boolean existsByInstitutionCode(String institutionCode);
    boolean existsByName(String name);
}
