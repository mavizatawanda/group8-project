package com.devops.qvs.controller;

import com.devops.qvs.dto.QualificationRequest;
import com.devops.qvs.dto.QualificationResponse;
import com.devops.qvs.service.QualificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qualifications")
@Tag(name = "Qualifications", description = "Endpoints for managing academic and professional qualification records")
public class QualificationController {

    private final QualificationService qualificationService;

    public QualificationController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_INSTITUTION')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Register Qualification", description = "Institutions or Admins can register a new qualification certificate")
    public ResponseEntity<QualificationResponse> registerQualification(@Valid @RequestBody QualificationRequest request) {
        QualificationResponse created = qualificationService.registerQualification(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List Qualifications", description = "Retrieve all registered qualification records")
    public ResponseEntity<List<QualificationResponse>> getAllQualifications() {
        return ResponseEntity.ok(qualificationService.getAllQualifications());
    }

    @GetMapping("/{certificateNumber}")
    @Operation(summary = "Get by Certificate Number", description = "Fetch qualification details using certificate serial number")
    public ResponseEntity<QualificationResponse> getByCertificateNumber(@PathVariable String certificateNumber) {
        return ResponseEntity.ok(qualificationService.getByCertificateNumber(certificateNumber));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Qualifications", description = "Search by student name, student ID, award title, or certificate number")
    public ResponseEntity<List<QualificationResponse>> searchQualifications(@RequestParam("q") String query) {
        return ResponseEntity.ok(qualificationService.search(query));
    }

    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Revoke Qualification", description = "Revoke a previously issued qualification with an audit reason")
    public ResponseEntity<QualificationResponse> revokeQualification(@PathVariable Long id,
                                                                      @RequestParam("reason") String reason) {
        return ResponseEntity.ok(qualificationService.revokeQualification(id, reason));
    }
}
