package com.devops.qvs.controller;

import com.devops.qvs.dto.VerificationRequest;
import com.devops.qvs.repository.InstitutionRepository;
import com.devops.qvs.repository.QualificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests: Zimbabwe State University Qualifications & Verification")
class ZimbabweUniversitiesVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QualificationRepository qualificationRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Test
    @DisplayName("Should have at least 70 qualifications in the database")
    void testTotalQualificationsCountAboveSeventy() {
        long count = qualificationRepository.count();
        assertTrue(count >= 70, "Database should contain at least 70 qualification records but found " + count);
    }

    @Test
    @DisplayName("Should verify ZOU IT qualification by Student ID (ITM 190020)")
    void testVerifyZouItDegreeByStudentId() throws Exception {
        VerificationRequest request = VerificationRequest.builder()
                .query("ITM 190020")
                .verifierName("Tech Recruiter Harare")
                .build();

        mockMvc.perform(post("/api/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("GENUINE_AND_VALID"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.studentFullName").value("Itai Muringani"))
                .andExpect(jsonPath("$.studentIdNumber").value("ITM 190020"))
                .andExpect(jsonPath("$.awardTitle").value("Bachelor of Science Honours in Information Technology"))
                .andExpect(jsonPath("$.institutionName").value("Zimbabwe Open University"));
    }

    @Test
    @DisplayName("Should verify ZOU PGDE qualification by Student ID (EH250001)")
    void testVerifyZouPgdeByStudentId() throws Exception {
        VerificationRequest request = VerificationRequest.builder()
                .query("EH250001")
                .verifierName("Ministry of Primary and Secondary Education")
                .build();

        mockMvc.perform(post("/api/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("GENUINE_AND_VALID"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.studentFullName").value("Sekai Mataranyika"))
                .andExpect(jsonPath("$.studentIdNumber").value("EH250001"))
                .andExpect(jsonPath("$.awardTitle").value("Postgraduate Diploma in Education (Mathematics)"))
                .andExpect(jsonPath("$.institutionName").value("Zimbabwe Open University"));
    }

    @Test
    @DisplayName("Should verify UZ BSc Computer Science qualification via GET endpoint")
    void testVerifyUzQualification() throws Exception {
        mockMvc.perform(get("/api/v1/verify/UZ-2024-BSC-3112"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("GENUINE_AND_VALID"))
                .andExpect(jsonPath("$.studentFullName").value("Tendai Moyo"))
                .andExpect(jsonPath("$.studentIdNumber").value("R203112B"))
                .andExpect(jsonPath("$.awardTitle").value("Bachelor of Science Honours in Computer Science"))
                .andExpect(jsonPath("$.institutionName").value("University of Zimbabwe"));
    }

    @Test
    @DisplayName("Should verify NUST and HIT records are present and searchable")
    void testSearchNustAndHitQualifications() throws Exception {
        mockMvc.perform(get("/api/v1/qualifications/search?q=NUST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(10)));

        mockMvc.perform(get("/api/v1/qualifications/search?q=HIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(10)));
    }
}
