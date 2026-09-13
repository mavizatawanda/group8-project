package com.devops.qvs.service;

import com.devops.qvs.dto.AuditLogDto;
import com.devops.qvs.model.AuditLog;
import com.devops.qvs.model.VerificationStatus;
import com.devops.qvs.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: Audit Logging & Traceability Service")
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog sampleLog;

    @BeforeEach
    void setUp() {
        sampleLog = AuditLog.builder()
                .id(1L)
                .queryParameter("CERT-101")
                .verificationOutcome(VerificationStatus.GENUINE_AND_VALID)
                .verifierIdentifier("HR Officer")
                .ipAddress("10.0.0.1")
                .userAgent("Mozilla")
                .certificateNumber("CERT-101")
                .institutionName("Oxford Tech")
                .details("Verified successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully record audit log entry")
    void testRecordAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(sampleLog);

        AuditLog saved = auditLogService.recordAuditLog(
                "CERT-101",
                VerificationStatus.GENUINE_AND_VALID,
                "HR Officer",
                "10.0.0.1",
                "Mozilla",
                "CERT-101",
                "Oxford Tech",
                "Verified successfully"
        );

        assertNotNull(saved);
        assertEquals(VerificationStatus.GENUINE_AND_VALID, saved.getVerificationOutcome());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should retrieve recent audit logs mapped to DTOs")
    void testGetRecentAuditLogs() {
        when(auditLogRepository.findTop50ByOrderByTimestampDesc()).thenReturn(List.of(sampleLog));

        List<AuditLogDto> dtos = auditLogService.getRecentAuditLogs();

        assertEquals(1, dtos.size());
        assertEquals("CERT-101", dtos.get(0).getCertificateNumber());
    }
}
