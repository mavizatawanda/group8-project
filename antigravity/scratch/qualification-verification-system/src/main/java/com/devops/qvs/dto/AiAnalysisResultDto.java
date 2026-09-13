package com.devops.qvs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResultDto {
    private String certificateNumber;
    private int authenticityScore; // 0 to 100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private double confidencePercentage;
    private List<String> anomalyFlags;
    private List<String> passedChecks;
    private String recommendation;
    private String agentExplanation;
    private LocalDateTime analyzedAt;
}
