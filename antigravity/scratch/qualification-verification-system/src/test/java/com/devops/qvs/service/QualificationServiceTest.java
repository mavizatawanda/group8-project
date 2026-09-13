package com.devops.qvs.service;

import com.devops.qvs.dto.QualificationRequest;
import com.devops.qvs.dto.QualificationResponse;
import com.devops.qvs.exception.BadRequestException;
import com.devops.qvs.exception.ResourceNotFoundException;
import com.devops.qvs.model.Institution;
import com.devops.qvs.model.Qualification;
import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.repository.InstitutionRepository;
import com.devops.qvs.repository.QualificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: Qualification Management Service")
class QualificationServiceTest {

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private CryptoHashService cryptoHashService;

    @Mock
    private BlockchainLedgerService blockchainLedgerService;

    @InjectMocks
    private QualificationService qualificationService;

    private Institution sampleInstitution;
    private Qualification sampleQualification;
    private QualificationRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleInstitution = Institution.builder()
                .id(1L)
                .name("Tech University")
                .institutionCode("TU-01")
                .build();

        sampleQualification = Qualification.builder()
                .id(100L)
                .certificateNumber("QVS-2024-ENG-01")
                .studentFullName("Jane Doe")
                .studentIdNumber("STU-123")
                .awardTitle("BSc Software Engineering")
                .majorSpecialization("DevOps")
                .classification("First Class")
                .awardDate(LocalDate.of(2024, 6, 1))
                .institution(sampleInstitution)
                .status(QualificationStatus.ACTIVE)
                .digitalFingerprint("sample_mock_hash_12345")
                .build();

        sampleRequest = QualificationRequest.builder()
                .certificateNumber("QVS-2024-ENG-01")
                .studentFullName("Jane Doe")
                .studentIdNumber("STU-123")
                .awardTitle("BSc Software Engineering")
                .majorSpecialization("DevOps")
                .classification("First Class")
                .awardDate(LocalDate.of(2024, 6, 1))
                .institutionId(1L)
                .build();
    }

    @Test
    @DisplayName("Should successfully register qualification with hash generation")
    void testRegisterQualificationSuccess() {
        when(qualificationRepository.existsByCertificateNumber(sampleRequest.getCertificateNumber())).thenReturn(false);
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(sampleInstitution));
        when(cryptoHashService.generateFingerprint(anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDate.class)))
                .thenReturn("generated_hash_abc");
        when(qualificationRepository.save(any(Qualification.class))).thenReturn(sampleQualification);

        QualificationResponse response = qualificationService.registerQualification(sampleRequest);

        assertNotNull(response);
        assertEquals("QVS-2024-ENG-01", response.getCertificateNumber());
        assertEquals("Jane Doe", response.getStudentFullName());
        verify(qualificationRepository).save(any(Qualification.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if certificate number already registered")
    void testRegisterDuplicateCertificateThrowsException() {
        when(qualificationRepository.existsByCertificateNumber(sampleRequest.getCertificateNumber())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> qualificationService.registerQualification(sampleRequest));
        verify(qualificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve qualification by certificate number")
    void testGetByCertificateNumberSuccess() {
        when(qualificationRepository.findByCertificateNumber("QVS-2024-ENG-01")).thenReturn(Optional.of(sampleQualification));

        QualificationResponse response = qualificationService.getByCertificateNumber("QVS-2024-ENG-01");

        assertNotNull(response);
        assertEquals("Jane Doe", response.getStudentFullName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if certificate not found")
    void testGetByCertificateNumberNotFound() {
        when(qualificationRepository.findByCertificateNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> qualificationService.getByCertificateNumber("UNKNOWN"));
    }

    @Test
    @DisplayName("Should revoke qualification with valid reason")
    void testRevokeQualification() {
        when(qualificationRepository.findById(100L)).thenReturn(Optional.of(sampleQualification));
        when(qualificationRepository.save(any(Qualification.class))).thenAnswer(i -> i.getArgument(0));

        QualificationResponse response = qualificationService.revokeQualification(100L, "Disciplinary action");

        assertNotNull(response);
        assertEquals(QualificationStatus.REVOKED, response.getStatus());
        assertEquals("Disciplinary action", response.getRevocationReason());
    }

    @Test
    @DisplayName("Should search qualifications by keyword")
    void testSearchQualifications() {
        when(qualificationRepository.searchQualifications("Jane")).thenReturn(List.of(sampleQualification));

        List<QualificationResponse> results = qualificationService.search("Jane");

        assertEquals(1, results.size());
        assertEquals("Jane Doe", results.get(0).getStudentFullName());
    }
}
