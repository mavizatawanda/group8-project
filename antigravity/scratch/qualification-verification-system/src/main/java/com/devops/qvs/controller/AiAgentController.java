package com.devops.qvs.controller;

import com.devops.qvs.dto.AiAnalysisResultDto;
import com.devops.qvs.dto.AiChatRequest;
import com.devops.qvs.dto.AiChatResponse;
import com.devops.qvs.service.AiFraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Agentic AI Fraud Detection", description = "AI heuristic verification and conversational assistant endpoints")
public class AiAgentController {

    private final AiFraudDetectionService aiFraudDetectionService;

    @GetMapping("/analyze/{certificateNumber}")
    @Operation(summary = "AI Fraud & Anomaly Analysis", description = "Executes multi-vector heuristic risk scoring on a certificate")
    public ResponseEntity<AiAnalysisResultDto> analyzeCertificate(@PathVariable String certificateNumber) {
        return ResponseEntity.ok(aiFraudDetectionService.analyzeCertificate(certificateNumber));
    }

    @PostMapping("/chat")
    @Operation(summary = "AI Verification Assistant Chat", description = "Conversational assistant for verification queries")
    public ResponseEntity<AiChatResponse> chatWithAgent(@RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiFraudDetectionService.processUserQuery(request));
    }
}
