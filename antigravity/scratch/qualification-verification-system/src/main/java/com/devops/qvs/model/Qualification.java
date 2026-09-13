package com.devops.qvs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qualifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String certificateNumber;

    @Column(nullable = false, length = 150)
    private String studentFullName;

    @Column(nullable = false, length = 100)
    private String studentIdNumber;

    @Column(nullable = false, length = 200)
    private String awardTitle; // e.g., "Bachelor of Science in Computer Science"

    @Column(nullable = false, length = 100)
    private String majorSpecialization; // e.g., "Software Engineering"

    @Column(nullable = false, length = 100)
    private String classification; // e.g., "First Class Honours", "Distinction"

    @Column(nullable = false)
    private LocalDate awardDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private QualificationStatus status = QualificationStatus.ACTIVE;

    @Column(nullable = false, length = 128)
    private String digitalFingerprint; // SHA-256 tamper-evident hash

    @Column(length = 255)
    private String revocationReason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
