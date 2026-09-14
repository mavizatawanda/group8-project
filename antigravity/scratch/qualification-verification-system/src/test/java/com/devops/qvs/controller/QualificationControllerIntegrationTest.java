package com.devops.qvs.controller;

import com.devops.qvs.dto.AuthRequest;
import com.devops.qvs.dto.AuthResponse;
import com.devops.qvs.dto.QualificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests: Qualification Controller Endpoints")
class QualificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtainAccessToken(String username, String password) throws Exception {
        AuthRequest authRequest = new AuthRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        return authResponse.getToken();
    }

    @Test
    @DisplayName("Should list all seeded qualifications")
    void testGetAllQualifications() throws Exception {
        mockMvc.perform(get("/api/v1/qualifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("Should search qualifications by keyword")
    void testSearchQualifications() throws Exception {
        mockMvc.perform(get("/api/v1/qualifications/search?q=Moyo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentFullName").value("Tendai Moyo"));
    }

    @Test
    @DisplayName("Should register new qualification with valid JWT token")
    void testRegisterQualificationWithAuth() throws Exception {
        String token = obtainAccessToken("officer", "Officer@12345");
        String uniqueCert = "CERT-INT-" + System.currentTimeMillis();

        QualificationRequest request = QualificationRequest.builder()
                .certificateNumber(uniqueCert)
                .studentFullName("Jonathan Taylor")
                .studentIdNumber("STU-665511")
                .awardTitle("BSc in Computer Networks")
                .majorSpecialization("Distributed Systems")
                .classification("First Class")
                .awardDate(LocalDate.of(2024, 8, 1))
                .institutionId(1L)
                .build();

        mockMvc.perform(post("/api/v1/qualifications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.certificateNumber").value(uniqueCert))
                .andExpect(jsonPath("$.digitalFingerprint").isNotEmpty())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should reject unauthorized qualification registration without token")
    void testRegisterQualificationWithoutAuthFails() throws Exception {
        QualificationRequest request = QualificationRequest.builder()
                .certificateNumber("UNAUTH-CERT")
                .studentFullName("Hacker")
                .studentIdNumber("STU-000")
                .awardTitle("Fake Degree")
                .majorSpecialization("None")
                .classification("First Class")
                .awardDate(LocalDate.now())
                .institutionId(1L)
                .build();

        mockMvc.perform(post("/api/v1/qualifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
