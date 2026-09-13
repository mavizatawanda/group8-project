package com.devops.qvs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit Tests: Cryptographic Tamper-Evident Hashing Service")
class CryptoHashServiceTest {

    private CryptoHashService cryptoHashService;

    @BeforeEach
    void setUp() {
        cryptoHashService = new CryptoHashService();
        ReflectionTestUtils.setField(cryptoHashService, "salt", "TEST-SALT-KEY-2026");
    }

    @Test
    @DisplayName("Should generate consistent SHA-256 fingerprint for identical inputs")
    void testGenerateFingerprintConsistency() {
        String certNum = "CERT-1001";
        String studentName = "Alice Smith";
        String studentId = "STU-5544";
        String award = "BSc Computer Science";
        String instCode = "INST-01";
        LocalDate date = LocalDate.of(2024, 5, 20);

        String hash1 = cryptoHashService.generateFingerprint(certNum, studentName, studentId, award, instCode, date);
        String hash2 = cryptoHashService.generateFingerprint(certNum, studentName, studentId, award, instCode, date);

        assertNotNull(hash1);
        assertEquals(64, hash1.length(), "SHA-256 hex string should be 64 characters long");
        assertEquals(hash1, hash2, "Hashing identical parameters must yield identical fingerprint");
    }

    @Test
    @DisplayName("Should successfully verify valid fingerprint")
    void testVerifyFingerprintSuccess() {
        String certNum = "CERT-2002";
        String studentName = "Bob Johnson";
        String studentId = "STU-7788";
        String award = "MSc Artificial Intelligence";
        String instCode = "INST-02";
        LocalDate date = LocalDate.of(2023, 10, 15);

        String generatedHash = cryptoHashService.generateFingerprint(certNum, studentName, studentId, award, instCode, date);
        boolean isValid = cryptoHashService.verifyFingerprint(certNum, studentName, studentId, award, instCode, date, generatedHash);

        assertTrue(isValid, "Fingerprint verification must pass for matching credential data");
    }

    @Test
    @DisplayName("Should detect tampering when credential data is altered")
    void testDetectTamperedCredentialData() {
        String certNum = "CERT-3003";
        String studentName = "Charlie Davis";
        String studentId = "STU-9900";
        String award = "BSc Cyber Security";
        String instCode = "INST-03";
        LocalDate date = LocalDate.of(2024, 1, 10);

        String legitimateHash = cryptoHashService.generateFingerprint(certNum, studentName, studentId, award, instCode, date);

        // Attempt verification with modified/tampered student name
        boolean tamperedNameCheck = cryptoHashService.verifyFingerprint(
                certNum, "Charlie Davis FORGED", studentId, award, instCode, date, legitimateHash
        );
        assertFalse(tamperedNameCheck, "Tampered student name must fail hash verification");

        // Attempt verification with modified award title
        boolean tamperedAwardCheck = cryptoHashService.verifyFingerprint(
                certNum, studentName, studentId, "PhD in Artificial Intelligence", instCode, date, legitimateHash
        );
        assertFalse(tamperedAwardCheck, "Tampered award title must fail hash verification");
    }
}
