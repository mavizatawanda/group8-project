package com.devops.qvs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricsDto {
    private long totalQualifications;
    private long activeQualifications;
    private long revokedQualifications;
    private long totalVerifications;
    private long genuineVerifications;
    private long failedVerifications;
    private long totalBlocks;
    private boolean blockchainHealthy;
    private long usedMemoryBytes;
    private long totalMemoryBytes;
    private long freeMemoryBytes;
    private int availableProcessors;
    private long systemUptimeSeconds;
    private double verificationSuccessRate;
    private LocalDateTime timestamp;
}
