package com.devops.qvs.dto;

import com.devops.qvs.model.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private String queryParameter;
    private VerificationStatus verificationOutcome;
    private String verifierIdentifier;
    private String ipAddress;
    private String userAgent;
    private String certificateNumber;
    private String institutionName;
    private String details;
    private LocalDateTime timestamp;
}
