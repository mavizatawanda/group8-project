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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QualificationService {

    private final QualificationRepository qualificationRepository;
    private final InstitutionRepository institutionRepository;
    private final CryptoHashService cryptoHashService;
    private final BlockchainLedgerService blockchainLedgerService;

    public QualificationService(QualificationRepository qualificationRepository,
                                InstitutionRepository institutionRepository,
                                CryptoHashService cryptoHashService,
                                BlockchainLedgerService blockchainLedgerService) {
        this.qualificationRepository = qualificationRepository;
        this.institutionRepository = institutionRepository;
        this.cryptoHashService = cryptoHashService;
        this.blockchainLedgerService = blockchainLedgerService;
    }

    @Transactional
    public QualificationResponse registerQualification(QualificationRequest request) {
        if (qualificationRepository.existsByCertificateNumber(request.getCertificateNumber())) {
            throw new BadRequestException("Qualification with certificate number " + request.getCertificateNumber() + " already exists.");
        }

        Institution institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + request.getInstitutionId()));

        String fingerprint = cryptoHashService.generateFingerprint(
                request.getCertificateNumber(),
                request.getStudentFullName(),
                request.getStudentIdNumber(),
                request.getAwardTitle(),
                institution.getInstitutionCode(),
                request.getAwardDate()
        );

        Qualification qualification = Qualification.builder()
                .certificateNumber(request.getCertificateNumber().trim().toUpperCase())
                .studentFullName(request.getStudentFullName().trim())
                .studentIdNumber(request.getStudentIdNumber().trim())
                .awardTitle(request.getAwardTitle().trim())
                .majorSpecialization(request.getMajorSpecialization().trim())
                .classification(request.getClassification().trim())
                .awardDate(request.getAwardDate())
                .institution(institution)
                .status(QualificationStatus.ACTIVE)
                .digitalFingerprint(fingerprint)
                .createdAt(LocalDateTime.now())
                .build();

        Qualification saved = qualificationRepository.save(qualification);

        // Notarize on the cryptographic blockchain ledger
        try {
            blockchainLedgerService.mineBlock(
                    saved.getCertificateNumber(),
                    saved.getDigitalFingerprint(),
                    "ISSUE",
                    institution.getName()
            );
        } catch (Exception e) {
            log.warn("Non-fatal: Blockchain block notarization encountered an issue: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QualificationResponse> getAllQualifications() {
        return qualificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QualificationResponse getByCertificateNumber(String certNumber) {
        Qualification qualification = qualificationRepository.findByCertificateNumber(certNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with certificate number: " + certNumber));
        return mapToResponse(qualification);
    }

    @Transactional(readOnly = true)
    public List<QualificationResponse> search(String query) {
        return qualificationRepository.searchQualifications(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QualificationResponse revokeQualification(Long id, String reason) {
        Qualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + id));

        qualification.setStatus(QualificationStatus.REVOKED);
        qualification.setRevocationReason(reason);
        qualification.setUpdatedAt(LocalDateTime.now());

        Qualification saved = qualificationRepository.save(qualification);

        // Notarize revocation on the blockchain
        try {
            blockchainLedgerService.mineBlock(
                    saved.getCertificateNumber(),
                    saved.getDigitalFingerprint(),
                    "REVOKE",
                    "SYSTEM_ADMIN"
            );
        } catch (Exception e) {
            log.warn("Non-fatal: Blockchain revocation notarization encountered an issue: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    public QualificationResponse mapToResponse(Qualification q) {
        return QualificationResponse.builder()
                .id(q.getId())
                .certificateNumber(q.getCertificateNumber())
                .studentFullName(q.getStudentFullName())
                .studentIdNumber(q.getStudentIdNumber())
                .awardTitle(q.getAwardTitle())
                .majorSpecialization(q.getMajorSpecialization())
                .classification(q.getClassification())
                .awardDate(q.getAwardDate())
                .institutionName(q.getInstitution() != null ? q.getInstitution().getName() : "N/A")
                .institutionCode(q.getInstitution() != null ? q.getInstitution().getInstitutionCode() : "N/A")
                .status(q.getStatus())
                .digitalFingerprint(q.getDigitalFingerprint())
                .revocationReason(q.getRevocationReason())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
