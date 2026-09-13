package com.devops.qvs.controller;

import com.devops.qvs.dto.BlockDto;
import com.devops.qvs.service.BlockchainLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
@Tag(name = "Blockchain Ledger", description = "Cryptographic blockchain verification and block explorer endpoints")
public class BlockchainController {

    private final BlockchainLedgerService blockchainLedgerService;

    @GetMapping("/chain")
    @Operation(summary = "Get full blockchain ledger", description = "Returns chronological list of all mined blocks")
    public ResponseEntity<List<BlockDto>> getFullChain() {
        return ResponseEntity.ok(blockchainLedgerService.getAllBlocks());
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate blockchain integrity", description = "Performs cryptographic verification of the entire block chain linkage")
    public ResponseEntity<Map<String, Object>> validateChain() {
        boolean valid = blockchainLedgerService.validateChain();
        long height = blockchainLedgerService.getChainHeight();

        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        response.put("chainHeight", height);
        response.put("status", valid ? "SECURE_INTEGRITY_VERIFIED" : "CHAIN_INTEGRITY_FAILED");
        response.put("message", valid
                ? "Cryptographic consensus confirmed. All block signatures and hash links are 100% genuine and un-tampered."
                : "Integrity alert! A block payload or previous hash pointer mismatch has been detected.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/certificate/{certificateNumber}")
    @Operation(summary = "Get block notarization for certificate", description = "Fetches blockchain transaction blocks for a specific certificate")
    public ResponseEntity<List<BlockDto>> getCertificateBlocks(@PathVariable String certificateNumber) {
        return ResponseEntity.ok(blockchainLedgerService.getBlocksForCertificate(certificateNumber));
    }
}
