package com.devops.qvs.repository;

import com.devops.qvs.model.Institution;
import com.devops.qvs.model.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    Optional<Qualification> findByCertificateNumber(String certificateNumber);
    Optional<Qualification> findByDigitalFingerprint(String digitalFingerprint);
    boolean existsByCertificateNumber(String certificateNumber);
    List<Qualification> findByInstitution(Institution institution);
    List<Qualification> findByStudentIdNumber(String studentIdNumber);

    @Query("SELECT q FROM Qualification q WHERE " +
           "LOWER(q.studentFullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.studentIdNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.certificateNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.awardTitle) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Qualification> searchQualifications(@Param("query") String query);
}
