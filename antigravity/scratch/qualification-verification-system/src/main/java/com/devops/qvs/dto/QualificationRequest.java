package com.devops.qvs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationRequest {

    @NotBlank(message = "Certificate number is required")
    private String certificateNumber;

    @NotBlank(message = "Student full name is required")
    private String studentFullName;

    @NotBlank(message = "Student ID / National ID is required")
    private String studentIdNumber;

    @NotBlank(message = "Award title is required")
    private String awardTitle;

    @NotBlank(message = "Major / Specialization is required")
    private String majorSpecialization;

    @NotBlank(message = "Classification is required")
    private String classification;

    @NotNull(message = "Award date is required")
    private LocalDate awardDate;

    private Long institutionId;
}
