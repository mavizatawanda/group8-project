package com.devops.qvs.dto;

import com.devops.qvs.model.QualificationStatus;
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
public class QualificationResponse {
    private Long id;
    private String certificateNumber;
    private String studentFullName;
    private String studentIdNumber;
    private String awardTitle;
    private String majorSpecialization;
    private String classification;
    private LocalDate awardDate;
    private String institutionName;
    private String institutionCode;
    private QualificationStatus status;
    private String digitalFingerprint;
    private String revocationReason;
    private LocalDateTime createdAt;
}
