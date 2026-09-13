package com.devops.qvs.service;

import com.devops.qvs.dto.VerificationRequest;
import com.devops.qvs.dto.VerificationResultDto;
import com.devops.qvs.model.AuditLog;
import com.devops.qvs.model.Qualification;
import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.model.VerificationStatus;
import com.devops.qvs.repository.QualificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService {

    private final QualificationRepository qualificationRepository;
    private final CryptoHashService cryptoHashService;
    private final AuditLogService auditLogService;

    public VerificationService(QualificationRepository qualificationRepository,
                               CryptoHashService cryptoHashService,
                               AuditLogService auditLogService) {
        this.qualificationRepository = qualificationRepository;
        this.cryptoHashService = cryptoHashService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public VerificationResultDto verifyQualification(VerificationRequest request,
                                                     String clientIp,
                                                     String userAgent) {
        String cleanQuery = request.getQuery().trim();

        // Check by Certificate Number, Digital Fingerprint, or Student ID
        Optional<Qualification> opt = qualificationRepository.findByCertificateNumber(cleanQuery.toUpperCase());
        if (opt.isEmpty()) {
            opt = qualificationRepository.findByCertificateNumber(cleanQuery);
        }
        if (opt.isEmpty()) {
            opt = qualificationRepository.findByDigitalFingerprint(cleanQuery);
        }
        if (opt.isEmpty()) {
            java.util.List<Qualification> byStudentId = qualificationRepository.findByStudentIdNumber(cleanQuery);
            if (!byStudentId.isEmpty()) {
                opt = Optional.of(byStudentId.get(0));
            } else {
                byStudentId = qualificationRepository.findByStudentIdNumber(cleanQuery.toUpperCase());
                if (!byStudentId.isEmpty()) {
                    opt = Optional.of(byStudentId.get(0));
                }
            }
        }

        String verifierId = (request.getVerifierName() != null && !request.getVerifierName().isBlank())
                ? request.getVerifierName() + " (" + (request.getVerifierOrganization() != null ? request.getVerifierOrganization() : "Individual") + ")"
                : "Public Verification Portal";

        if (opt.isEmpty()) {
            // Record Audit Failure
            AuditLog log = auditLogService.recordAuditLog(
                    cleanQuery,
                    VerificationStatus.RECORD_NOT_FOUND,
                    verifierId,
                    clientIp,
                    userAgent,
                    cleanQuery,
                    "Unknown Institution",
                    "Verification failed: No matching qualification record found in registry."
            );

            return VerificationResultDto.builder()
                    .verificationStatus(VerificationStatus.RECORD_NOT_FOUND)
                    .message("No authentic academic record found matching query '" + cleanQuery + "'. Please check the certificate number.")
                    .valid(false)
                    .tamperProofCheckPassed(false)
                    .verifiedAt(LocalDateTime.now())
                    .auditLogId(log.getId())
                    .build();
        }

        Qualification q = opt.get();

        // Check if certificate is Revoked
        if (q.getStatus() == QualificationStatus.REVOKED) {
            AuditLog log = auditLogService.recordAuditLog(
                    cleanQuery,
                    VerificationStatus.REVOKED_CREDENTIAL,
                    verifierId,
                    clientIp,
                    userAgent,
                    q.getCertificateNumber(),
                    q.getInstitution().getName(),
                    "Verification warning: Qualification was revoked. Reason: " + q.getRevocationReason()
            );

            return mapToResult(q, VerificationStatus.REVOKED_CREDENTIAL,
                    "WARNING: This qualification was revoked by the issuing institution (" + q.getRevocationReason() + ").",
                    false, true, log.getId());
        }

        // Cryptographic Tamper-Proof Check (Calculate runtime hash and compare against stored signature)
        boolean isTamperProof = cryptoHashService.verifyFingerprint(
                q.getCertificateNumber(),
                q.getStudentFullName(),
                q.getStudentIdNumber(),
                q.getAwardTitle(),
                q.getInstitution().getInstitutionCode(),
                q.getAwardDate(),
                q.getDigitalFingerprint()
        );

        if (!isTamperProof) {
            AuditLog log = auditLogService.recordAuditLog(
                    cleanQuery,
                    VerificationStatus.SUSPICIOUS_OR_ALTERED,
                    verifierId,
                    clientIp,
                    userAgent,
                    q.getCertificateNumber(),
                    q.getInstitution().getName(),
                    "CRITICAL SECURITY ALERT: Cryptographic hash mismatch! Data may have been tampered with."
            );

            return mapToResult(q, VerificationStatus.SUSPICIOUS_OR_ALTERED,
                    "CRITICAL: Digital integrity check failed. The record data has been altered or tampered with.",
                    false, false, log.getId());
        }

        // Success - Authentic & Verified
        AuditLog log = auditLogService.recordAuditLog(
                cleanQuery,
                VerificationStatus.GENUINE_AND_VALID,
                verifierId,
                clientIp,
                userAgent,
                q.getCertificateNumber(),
                q.getInstitution().getName(),
                "Authenticity confirmed. Validated via SHA-256 digital fingerprint."
        );

        return mapToResult(q, VerificationStatus.GENUINE_AND_VALID,
                "AUTHENTIC & VALID: Qualification successfully verified against the institutional registry.",
                true, true, log.getId());
    }

    private VerificationResultDto mapToResult(Qualification q,
                                              VerificationStatus status,
                                              String message,
                                              boolean valid,
                                              boolean tamperPassed,
                                              Long auditLogId) {
        return VerificationResultDto.builder()
                .verificationStatus(status)
                .message(message)
                .valid(valid)
                .tamperProofCheckPassed(tamperPassed)
                .certificateNumber(q.getCertificateNumber())
                .studentFullName(q.getStudentFullName())
                .studentIdNumber(q.getStudentIdNumber())
                .awardTitle(q.getAwardTitle())
                .majorSpecialization(q.getMajorSpecialization())
                .classification(q.getClassification())
                .awardDate(q.getAwardDate())
                .institutionName(q.getInstitution() != null ? q.getInstitution().getName() : "N/A")
                .qualificationStatus(q.getStatus())
                .digitalFingerprint(q.getDigitalFingerprint())
                .revocationReason(q.getRevocationReason())
                .verifiedAt(LocalDateTime.now())
                .auditLogId(auditLogId)
                .build();
    }
}
