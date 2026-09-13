package com.devops.qvs.controller;

import com.devops.qvs.dto.SystemMetricsDto;
import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.model.VerificationStatus;
import com.devops.qvs.repository.AuditLogRepository;
import com.devops.qvs.repository.BlockRepository;
import com.devops.qvs.repository.QualificationRepository;
import com.devops.qvs.service.BlockchainLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Tag(name = "Real-Time Telemetry & Monitoring", description = "Live system health and verification telemetry metrics")
public class MetricsController {

    private final QualificationRepository qualificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final BlockRepository blockRepository;
    private final BlockchainLedgerService blockchainLedgerService;

    @GetMapping("/realtime")
    @Operation(summary = "Real-Time System Telemetry", description = "Fetches live JVM, verification velocity, and blockchain metrics")
    public ResponseEntity<SystemMetricsDto> getRealtimeMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        long usedMem = totalMem - freeMem;
        int processors = runtime.availableProcessors();
        long uptimeSec = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        long totalQuals = qualificationRepository.count();
        long activeQuals = qualificationRepository.findAll().stream()
                .filter(q -> q.getStatus() == QualificationStatus.ACTIVE)
                .count();
        long revokedQuals = totalQuals - activeQuals;

        long totalVerifs = auditLogRepository.count();
        long genuineVerifs = auditLogRepository.findAll().stream()
                .filter(a -> a.getVerificationOutcome() == VerificationStatus.GENUINE_AND_VALID)
                .count();
        long failedVerifs = totalVerifs - genuineVerifs;

        double successRate = totalVerifs > 0 ? ((double) genuineVerifs / totalVerifs) * 100.0 : 100.0;
        long totalBlocks = blockRepository.count();
        boolean chainValid = blockchainLedgerService.validateChain();

        SystemMetricsDto dto = SystemMetricsDto.builder()
                .totalQualifications(totalQuals)
                .activeQualifications(activeQuals)
                .revokedQualifications(revokedQuals)
                .totalVerifications(totalVerifs)
                .genuineVerifications(genuineVerifs)
                .failedVerifications(failedVerifs)
                .totalBlocks(totalBlocks)
                .blockchainHealthy(chainValid)
                .usedMemoryBytes(usedMem)
                .totalMemoryBytes(totalMem)
                .freeMemoryBytes(freeMem)
                .availableProcessors(processors)
                .systemUptimeSeconds(uptimeSec)
                .verificationSuccessRate(Math.round(successRate * 10.0) / 10.0)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(dto);
    }
}
