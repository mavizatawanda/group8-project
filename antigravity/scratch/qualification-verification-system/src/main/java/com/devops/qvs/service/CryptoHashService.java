package com.devops.qvs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@Service
public class CryptoHashService {

    @Value("${app.crypto.salt:QVS-DEFAULT-SALT}")
    private String salt;

    /**
     * Calculates a deterministic, tamper-evident SHA-256 cryptographic hash of qualification data.
     */
    public String generateFingerprint(String certificateNumber,
                                      String studentFullName,
                                      String studentIdNumber,
                                      String awardTitle,
                                      String institutionCode,
                                      LocalDate awardDate) {
        String payload = String.format("%s|%s|%s|%s|%s|%s|%s",
                certificateNumber.trim().toUpperCase(),
                studentFullName.trim().toUpperCase(),
                studentIdNumber.trim().toUpperCase(),
                awardTitle.trim().toUpperCase(),
                institutionCode.trim().toUpperCase(),
                awardDate.toString(),
                salt);

        return sha256(payload);
    }

    /**
     * Verifies if given qualification parameters match an expected digital fingerprint.
     */
    public boolean verifyFingerprint(String certificateNumber,
                                     String studentFullName,
                                     String studentIdNumber,
                                     String awardTitle,
                                     String institutionCode,
                                     LocalDate awardDate,
                                     String expectedFingerprint) {
        String computed = generateFingerprint(certificateNumber, studentFullName, studentIdNumber, awardTitle, institutionCode, awardDate);
        return computed.equalsIgnoreCase(expectedFingerprint);
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
