package com.devops.qvs.repository;

import com.devops.qvs.model.AuditLog;
import com.devops.qvs.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop50ByOrderByTimestampDesc();
    List<AuditLog> findByCertificateNumber(String certificateNumber);
    List<AuditLog> findByVerificationOutcome(VerificationStatus outcome);
}
