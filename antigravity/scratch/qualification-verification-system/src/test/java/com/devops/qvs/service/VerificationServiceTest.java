package com.devops.qvs.service;

import com.devops.qvs.dto.VerificationRequest;
import com.devops.qvs.dto.VerificationResultDto;
import com.devops.qvs.model.AuditLog;
import com.devops.qvs.model.Institution;
import com.devops.qvs.model.Qualification;
import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.model.VerificationStatus;
import com.devops.qvs.repository.QualificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: Qualification Verification & Integrity Engine")
class VerificationServiceTest {

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private CryptoHashService cryptoHashService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private VerificationService verificationService;

    private Qualification sampleQualification;
    private Institution sampleInstitution;

    @BeforeEach
    void setUp() {
        sampleInstitution = Institution.builder()
                .id(1L)
                .name("Oxford Global Institute")
                .institutionCode("OGI-01")
                .build();

        sampleQualification = Qualification.builder()
                .id(50L)
                .certificateNumber("QVS-2024-ENG-7711")
                .studentFullName("Alice Wonderland")
                .studentIdNumber("STU-8822")
                .awardTitle("BSc Computer Systems")
                .majorSpecialization("Software Architecture")
                .classification("First Class Honours")
                .awardDate(LocalDate.of(2024, 7, 10))
                .institution(sampleInstitution)
                .status(QualificationStatus.ACTIVE)
                .digitalFingerprint("valid_sha256_hash_value")
                .build();
    }

    @Test
    @DisplayName("Should verify authentic and valid qualification successfully")
    void testVerifyAuthenticCredentialSuccess() {
        VerificationRequest request = VerificationRequest.builder()
                .query("QVS-2024-ENG-7711")
                .verifierName("Tech Recruiter")
                .verifierOrganization("Global HR")
                .build();

        when(qualificationRepository.findByCertificateNumber("QVS-2024-ENG-7711")).thenReturn(Optional.of(sampleQualification));
        when(cryptoHashService.verifyFingerprint(
                anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString()
        )).thenReturn(true);

        AuditLog sampleLog = AuditLog.builder().id(99L).build();
        when(auditLogService.recordAuditLog(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleLog);

        VerificationResultDto result = verificationService.verifyQualification(request, "192.168.1.1", "Chrome");

        assertNotNull(result);
        assertEquals(VerificationStatus.GENUINE_AND_VALID, result.getVerificationStatus());
        assertTrue(result.isValid());
        assertTrue(result.isTamperProofCheckPassed());
        assertEquals("Alice Wonderland", result.getStudentFullName());
        verify(auditLogService).recordAuditLog(
                eq("QVS-2024-ENG-7711"),
                eq(VerificationStatus.GENUINE_AND_VALID),
                anyString(),
                eq("192.168.1.1"),
                eq("Chrome"),
                eq("QVS-2024-ENG-7711"),
                eq("Oxford Global Institute"),
                anyString()
        );
    }

    @Test
    @DisplayName("Should flag and reject tampered credential data")
    void testVerifyTamperedCredential() {
        VerificationRequest request = VerificationRequest.builder()
                .query("QVS-2024-ENG-7711")
                .build();

        when(qualificationRepository.findByCertificateNumber("QVS-2024-ENG-7711")).thenReturn(Optional.of(sampleQualification));
        when(cryptoHashService.verifyFingerprint(
                anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString()
        )).thenReturn(false); // Hash Mismatch!

        AuditLog sampleLog = AuditLog.builder().id(100L).build();
        when(auditLogService.recordAuditLog(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleLog);

        VerificationResultDto result = verificationService.verifyQualification(request, "127.0.0.1", "Safari");

        assertNotNull(result);
        assertEquals(VerificationStatus.SUSPICIOUS_OR_ALTERED, result.getVerificationStatus());
        assertFalse(result.isValid());
        assertFalse(result.isTamperProofCheckPassed());
    }

    @Test
    @DisplayName("Should detect and warn if credential has been revoked")
    void testVerifyRevokedCredential() {
        sampleQualification.setStatus(QualificationStatus.REVOKED);
        sampleQualification.setRevocationReason("Falsified Entry Requirements");

        VerificationRequest request = VerificationRequest.builder()
                .query("QVS-2024-ENG-7711")
                .build();

        when(qualificationRepository.findByCertificateNumber("QVS-2024-ENG-7711")).thenReturn(Optional.of(sampleQualification));

        AuditLog sampleLog = AuditLog.builder().id(101L).build();
        when(auditLogService.recordAuditLog(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleLog);

        VerificationResultDto result = verificationService.verifyQualification(request, "127.0.0.1", "Edge");

        assertNotNull(result);
        assertEquals(VerificationStatus.REVOKED_CREDENTIAL, result.getVerificationStatus());
        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("Should handle unrecorded / non-existent credential query")
    void testVerifyNonExistentCredential() {
        VerificationRequest request = VerificationRequest.builder()
                .query("NON-EXISTENT-CERT")
                .build();

        when(qualificationRepository.findByCertificateNumber("NON-EXISTENT-CERT")).thenReturn(Optional.empty());
        when(qualificationRepository.findByDigitalFingerprint("NON-EXISTENT-CERT")).thenReturn(Optional.empty());

        AuditLog sampleLog = AuditLog.builder().id(102L).build();
        when(auditLogService.recordAuditLog(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleLog);

        VerificationResultDto result = verificationService.verifyQualification(request, "127.0.0.1", "Curl");

        assertNotNull(result);
        assertEquals(VerificationStatus.RECORD_NOT_FOUND, result.getVerificationStatus());
        assertFalse(result.isValid());
    }
}
