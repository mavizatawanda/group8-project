package com.devops.qvs.service;

import com.devops.qvs.dto.AuditLogDto;
import com.devops.qvs.model.AuditLog;
import com.devops.qvs.model.VerificationStatus;
import com.devops.qvs.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog recordAuditLog(String queryParam,
                                   VerificationStatus status,
                                   String verifierIdentifier,
                                   String ipAddress,
                                   String userAgent,
                                   String certNumber,
                                   String institutionName,
                                   String details) {
        AuditLog log = AuditLog.builder()
                .queryParameter(queryParam)
                .verificationOutcome(status)
                .verifierIdentifier(verifierIdentifier != null ? verifierIdentifier : "Anonymous Public Verifier")
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .userAgent(userAgent != null ? userAgent : "Web-Client")
                .certificateNumber(certNumber)
                .institutionName(institutionName)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getLogsByCertificateNumber(String certificateNumber) {
        return auditLogRepository.findByCertificateNumber(certificateNumber)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto mapToDto(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .queryParameter(log.getQueryParameter())
                .verificationOutcome(log.getVerificationOutcome())
                .verifierIdentifier(log.getVerifierIdentifier())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .certificateNumber(log.getCertificateNumber())
                .institutionName(log.getInstitutionName())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
