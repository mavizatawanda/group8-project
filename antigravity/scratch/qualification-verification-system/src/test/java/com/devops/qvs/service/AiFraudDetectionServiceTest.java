package com.devops.qvs.service;

import com.devops.qvs.dto.AiAnalysisResultDto;
import com.devops.qvs.dto.AiChatRequest;
import com.devops.qvs.dto.AiChatResponse;
import com.devops.qvs.model.Institution;
import com.devops.qvs.model.Qualification;
import com.devops.qvs.model.QualificationStatus;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: Agentic AI Fraud Detection & Assistant Service")
class AiFraudDetectionServiceTest {

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private CryptoHashService cryptoHashService;

    @InjectMocks
    private AiFraudDetectionService aiFraudDetectionService;

    private Institution sampleInstitution;
    private Qualification genuineQual;
    private Qualification revokedQual;

    @BeforeEach
    void setUp() {
        sampleInstitution = Institution.builder()
                .id(1L)
                .name("National Institute of Tech")
                .institutionCode("NIT-001")
                .active(true)
                .build();

        genuineQual = Qualification.builder()
                .id(101L)
                .certificateNumber("QVS-2024-BSC-8891")
                .studentFullName("Sarah Jenkins")
                .studentIdNumber("STU-990142")
                .awardTitle("Bachelor of Science in Software Engineering")
                .majorSpecialization("DevOps")
                .classification("First Class Honours")
                .awardDate(LocalDate.of(2024, 7, 15))
                .institution(sampleInstitution)
                .status(QualificationStatus.ACTIVE)
                .digitalFingerprint("valid_hash_12345")
                .build();

        revokedQual = Qualification.builder()
                .id(102L)
                .certificateNumber("QVS-2022-DIP-1109")
                .studentFullName("Marcus Vance")
                .studentIdNumber("STU-772911")
                .awardTitle("Diploma in Enterprise Systems")
                .majorSpecialization("Infrastructure")
                .classification("Second Class")
                .awardDate(LocalDate.of(2022, 6, 30))
                .institution(sampleInstitution)
                .status(QualificationStatus.REVOKED)
                .revocationReason("Disciplinary misconduct")
                .digitalFingerprint("revoked_hash_99999")
                .build();
    }

    @Test
    @DisplayName("Should assign LOW risk and 100 authenticity score for genuine valid certificate")
    void testAnalyzeGenuineCertificate() {
        when(qualificationRepository.findByCertificateNumber("QVS-2024-BSC-8891"))
                .thenReturn(Optional.of(genuineQual));
        when(cryptoHashService.verifyFingerprint(anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString()))
                .thenReturn(true);

        AiAnalysisResultDto result = aiFraudDetectionService.analyzeCertificate("QVS-2024-BSC-8891");

        assertNotNull(result);
        assertEquals("LOW", result.getRiskLevel());
        assertEquals(100, result.getAuthenticityScore());
        assertTrue(result.getPassedChecks().size() >= 3);
        assertTrue(result.getAnomalyFlags().isEmpty());
    }

    @Test
    @DisplayName("Should assign HIGH/CRITICAL risk when certificate is revoked")
    void testAnalyzeRevokedCertificate() {
        when(qualificationRepository.findByCertificateNumber("QVS-2022-DIP-1109"))
                .thenReturn(Optional.of(revokedQual));
        when(cryptoHashService.verifyFingerprint(anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString()))
                .thenReturn(true);

        AiAnalysisResultDto result = aiFraudDetectionService.analyzeCertificate("QVS-2022-DIP-1109");

        assertNotNull(result);
        assertTrue(result.getRiskLevel().equals("HIGH") || result.getRiskLevel().equals("CRITICAL"));
        assertTrue(result.getAuthenticityScore() < 50);
        assertTrue(result.getAnomalyFlags().stream().anyMatch(f -> f.contains("REVOKED")));
    }

    @Test
    @DisplayName("Should assign CRITICAL risk and 0 score when certificate does not exist in registry")
    void testAnalyzeNonExistentCertificate() {
        when(qualificationRepository.findByCertificateNumber("FAKE-999"))
                .thenReturn(Optional.empty());

        AiAnalysisResultDto result = aiFraudDetectionService.analyzeCertificate("FAKE-999");

        assertNotNull(result);
        assertEquals("CRITICAL", result.getRiskLevel());
        assertEquals(0, result.getAuthenticityScore());
    }

    @Test
    @DisplayName("Should respond intelligently to AI conversational user queries")
    void testChatWithAgent() {
        AiChatResponse response = aiFraudDetectionService.processUserQuery(
                AiChatRequest.builder().message("How do I verify a certificate?").build()
        );

        assertNotNull(response);
        assertEquals("INFO", response.getStatus());
        assertTrue(response.getReply().contains("How to Verify"));
    }

    @Test
    @DisplayName("Should answer questions about what the system does")
    void testChatWhatTheSystemDoes() {
        AiChatResponse response = aiFraudDetectionService.processUserQuery(
                AiChatRequest.builder().message("What does the system do?").build()
        );

        assertNotNull(response);
        assertEquals("SYSTEM_OVERVIEW", response.getIntent());
        assertTrue(response.getReply().contains("DevOps Qualification Verification System"));
    }

    @Test
    @DisplayName("Should answer questions about how to run the system")
    void testChatHowToRunTheSystem() {
        AiChatResponse response = aiFraudDetectionService.processUserQuery(
                AiChatRequest.builder().message("How to run the system?").build()
        );

        assertNotNull(response);
        assertEquals("HOW_TO_RUN", response.getIntent());
        assertTrue(response.getReply().contains("mvn spring-boot:run"));
        assertTrue(response.getReply().contains("run-app.bat"));
    }

    @Test
    @DisplayName("Should answer questions about how to log in")
    void testChatHowToLogIn() {
        AiChatResponse response = aiFraudDetectionService.processUserQuery(
                AiChatRequest.builder().message("How to log in to the system?").build()
        );

        assertNotNull(response);
        assertEquals("HOW_TO_LOGIN", response.getIntent());
        assertTrue(response.getReply().contains("admin"));
        assertTrue(response.getReply().contains("Admin@12345"));
    }
}
