package com.devops.qvs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @NotBlank(message = "Verification query (Certificate number, Student ID, or Digital Hash) is required")
    private String query;

    private String verifierName;
    private String verifierOrganization;
}
