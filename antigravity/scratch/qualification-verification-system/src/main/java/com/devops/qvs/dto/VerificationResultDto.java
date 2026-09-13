package com.devops.qvs.dto;

import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.model.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResultDto {
    private VerificationStatus verificationStatus;
    private String message;
    private boolean valid;
    private boolean tamperProofCheckPassed;

    // Credential details if verified
    private String certificateNumber;
    private String studentFullName;
    private String studentIdNumber;
    private String awardTitle;
    private String majorSpecialization;
    private String classification;
    private LocalDate awardDate;
    private String institutionName;
    private QualificationStatus qualificationStatus;
    private String digitalFingerprint;
    private String revocationReason;

    private LocalDateTime verifiedAt;
    private Long auditLogId;
}
