package com.devops.qvs.controller;

import com.devops.qvs.dto.AuditLogDto;
import com.devops.qvs.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Endpoints for inspecting auditable verification activity history")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "Get Recent Audit Logs", description = "Retrieve the latest 50 verification audit logs")
    public ResponseEntity<List<AuditLogDto>> getRecentAuditLogs() {
        return ResponseEntity.ok(auditLogService.getRecentAuditLogs());
    }

    @GetMapping("/certificate/{certNumber}")
    @Operation(summary = "Get Logs by Certificate", description = "Retrieve verification audit history for a specific certificate number")
    public ResponseEntity<List<AuditLogDto>> getLogsByCertificate(@PathVariable String certNumber) {
        return ResponseEntity.ok(auditLogService.getLogsByCertificateNumber(certNumber));
    }
}
