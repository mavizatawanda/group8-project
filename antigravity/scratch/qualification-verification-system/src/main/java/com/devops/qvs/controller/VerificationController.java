package com.devops.qvs.controller;

import com.devops.qvs.dto.VerificationRequest;
import com.devops.qvs.dto.VerificationResultDto;
import com.devops.qvs.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verify")
@Tag(name = "Verification", description = "Public & Institutional Qualification Verification Engine")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping
    @Operation(summary = "Verify Credential (POST)", description = "Verify qualification authenticity via Certificate Number, Student ID, or Digital Hash")
    public ResponseEntity<VerificationResultDto> verifyPost(@Valid @RequestBody VerificationRequest request,
                                                            HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");
        return ResponseEntity.ok(verificationService.verifyQualification(request, clientIp, userAgent));
    }

    @GetMapping("/{certificateNumber}")
    @Operation(summary = "Quick Verify (GET)", description = "Quick URL-based credential verification")
    public ResponseEntity<VerificationResultDto> verifyGet(@PathVariable String certificateNumber,
                                                           HttpServletRequest servletRequest) {
        VerificationRequest request = VerificationRequest.builder()
                .query(certificateNumber)
                .verifierName("Direct URL Verifier")
                .build();
        String clientIp = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");
        return ResponseEntity.ok(verificationService.verifyQualification(request, clientIp, userAgent));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
