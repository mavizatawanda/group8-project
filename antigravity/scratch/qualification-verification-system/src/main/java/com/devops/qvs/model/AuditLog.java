package com.devops.qvs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String queryParameter; // Certificate number or student ID queried

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VerificationStatus verificationOutcome;

    private String verifierIdentifier; // e.g., "Public Verifier" or User Email / Name

    private String ipAddress;

    private String userAgent;

    @Column(length = 100)
    private String certificateNumber;

    @Column(length = 200)
    private String institutionName;

    @Column(length = 500)
    private String details;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
