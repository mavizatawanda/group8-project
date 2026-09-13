package com.devops.qvs.controller;

import com.devops.qvs.dto.VerificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests: Qualification Verification Endpoints")
class VerificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should verify active seeded qualification via POST")
    void testVerifyActiveQualificationPost() throws Exception {
        VerificationRequest request = VerificationRequest.builder()
                .query("QVS-2024-BSC-8891")
                .verifierName("Employer Recruiter")
                .build();

        mockMvc.perform(post("/api/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("GENUINE_AND_VALID"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.tamperProofCheckPassed").value(true))
                .andExpect(jsonPath("$.studentFullName").value("Sarah Jenkins"))
                .andExpect(jsonPath("$.auditLogId").isNotEmpty());
    }

    @Test
    @DisplayName("Should verify active qualification via GET endpoint")
    void testVerifyActiveQualificationGet() throws Exception {
        mockMvc.perform(get("/api/v1/verify/QVS-2024-BSC-8891"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("GENUINE_AND_VALID"))
                .andExpect(jsonPath("$.studentFullName").value("Sarah Jenkins"));
    }

    @Test
    @DisplayName("Should flag revoked qualification")
    void testVerifyRevokedQualification() throws Exception {
        VerificationRequest request = VerificationRequest.builder()
                .query("QVS-2022-DIP-1109")
                .build();

        mockMvc.perform(post("/api/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("REVOKED_CREDENTIAL"))
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should return RECORD_NOT_FOUND for fictitious serial number")
    void testVerifyNonExistentRecord() throws Exception {
        VerificationRequest request = VerificationRequest.builder()
                .query("FICTITIOUS-CERT-12345")
                .build();

        mockMvc.perform(post("/api/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("RECORD_NOT_FOUND"))
                .andExpect(jsonPath("$.valid").value(false));
    }
}
