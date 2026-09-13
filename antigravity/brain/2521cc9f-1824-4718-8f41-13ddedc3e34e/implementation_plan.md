# Implementation Plan: Zimbabwe State Universities Qualifications Database Population

Populate the database with authentic Zimbabwe State University degrees and student records (80+ qualifications across 8 state universities), following exact student ID and degree formatting specifications, notarizing all credentials on the immutable blockchain ledger.

## User Review Required

> [!IMPORTANT]
> - **Zimbabwe State Universities**: We are seeding 8 top public state universities in Zimbabwe: UZ, NUST, MSU, ZOU, CUT, BUSE, GZU, and HIT.
> - **10 Students per University**: Each university contains 10 distinct students pursuing 10 different degree programs (totaling 80 new qualifications + 3 baseline = 83 qualifications, exceeding the 70+ requirement).
> - **ZOU Exact Formats**:
>   - For **IT Degrees**: Student ID numbers follow the requested `ITM 190020`, `ITM 200021`, etc.
>   - For **PGDE Degrees**: Student ID numbers follow `EH250001`, `EH240002`, `EH230003`, etc., where the first two digits represent the year (25 for 2025, 24 for 2024, etc.).
> - **Institutional Officer Accounts**: Dedicated registrar officer login accounts are created for universities (e.g. `officer_zou`, `officer_uz`, `officer_nust`, etc.) in addition to the master admin.

---

## Data Model & Formatting Plan

### 1. Zimbabwe Open University (ZOU)
- **Institution Code**: `ZOU-004`
- **Official Degree Programs & Formats**:
  1. **BSc Honours in Information Technology** — ID: `ITM 190020` | Cert: `ZOU-2023-ITM-190020` | Student: Itai Muringani
  2. **BSc Honours in Network Computing** — ID: `ITM 200021` | Cert: `ZOU-2024-ITM-200021` | Student: Rutendo Chitepo
  3. **BSc Honours in Software Engineering** — ID: `ITM 210022` | Cert: `ZOU-2025-ITM-210022` | Student: Memory Mandaza
  4. **BSc Honours in Multimedia Technology** — ID: `ITM 220023` | Cert: `ZOU-2026-ITM-220023` | Student: Kuda Marange
  5. **BSc Honours in Management Information Systems** — ID: `ITM 190024` | Cert: `ZOU-2023-ITM-190024` | Student: Learnmore Zimunya
  6. **Postgraduate Diploma in Education (Mathematics)** — ID: `EH250001` (Year 25) | Cert: `ZOU-2025-PGDE-EH250001` | Student: Sekai Mataranyika
  7. **Postgraduate Diploma in Education (Science)** — ID: `EH240002` (Year 24) | Cert: `ZOU-2024-PGDE-EH240002` | Student: Tapiwa Rusike
  8. **Postgraduate Diploma in Education (English Language)** — ID: `EH230003` (Year 23) | Cert: `ZOU-2023-PGDE-EH230003` | Student: Grace Mukaro
  9. **Postgraduate Diploma in Education (Educational Leadership)** — ID: `EH220004` (Year 22) | Cert: `ZOU-2022-PGDE-EH220004` | Student: Admire Charamba
  10. **Postgraduate Diploma in Education (ICT in Education)** — ID: `EH210005` (Year 21) | Cert: `ZOU-2021-PGDE-EH210005` | Student: Patience Mutungwazi

### 2. University of Zimbabwe (UZ)
- **Institution Code**: `UZ-001`
- **Degrees**:
  1. Bachelor of Science Honours in Computer Science (R203112B)
  2. Bachelor of Science Honours in Civil Engineering (R192034A)
  3. Bachelor of Medicine and Bachelor of Surgery (MBChB) (R184512M)
  4. Bachelor of Laws Honours (LLBS) (R214556C)
  5. Bachelor of Science Honours in Electrical and Electronic Engineering (R201098E)
  6. Bachelor of Science Honours in Pharmacy (R195678P)
  7. Bachelor of Science Honours in Economics (R221098D)
  8. Bachelor of Science Honours in Mining Engineering (R208765M)
  9. Bachelor of Science Honours in Agriculture (Crop Science) (R212345A)
  10. Master of Science in Data Analytics (R239871S)

### 3. National University of Science and Technology (NUST)
- **Institution Code**: `NUST-002`
- **Degrees**:
  1. Bachelor of Science Honours in Applied Mathematics (N0205678G)
  2. Bachelor of Science Honours in Chemical Engineering (N0191234F)
  3. Bachelor of Science Honours in Computer Science (N0214321H)
  4. Bachelor of Science Honours in Electronic Engineering (N0209876E)
  5. Bachelor of Science Honours in Applied Biology and Biochemistry (N0213456B)
  6. Bachelor of Science Honours in Industrial & Manufacturing Engineering (N0198765M)
  7. Bachelor of Science Honours in Architecture (N0187654A)
  8. Bachelor of Science Honours in Environmental Science and Health (N0228765K)
  9. Bachelor of Science Honours in Radiography (N0204321R)
  10. Master of Science in Information Systems (N0231290S)

### 4. Midlands State University (MSU)
- **Institution Code**: `MSU-003`
- **Degrees**:
  1. Bachelor of Science Honours in Information Systems (R2021334M)
  2. Bachelor of Commerce Honours in Accounting (R1910245P)
  3. Bachelor of Science Honours in Human Resource Management (R2132445K)
  4. Bachelor of Commerce Honours in Banking and Finance (R2045612B)
  5. Bachelor of Science Honours in Media and Society Studies (R1987654M)
  6. Bachelor of Science Honours in Telecommunications (R2156789T)
  7. Bachelor of Science Honours in Food Science and Technology (R2098765F)
  8. Bachelor of Laws Honours (LLB) (R1876543L)
  9. Bachelor of Science Honours in Psychology (R2243556T)
  10. Master of Commerce in Strategic Management (R2312098S)

### 5. Chinhoyi University of Technology (CUT)
- **Institution Code**: `CUT-005`
- **Degrees**:
  1. Bachelor of Technology Honours in Production Engineering (C2011234B)
  2. Bachelor of Science Honours in Mechatronics Engineering (C1910123A)
  3. Bachelor of Technology Honours in Biotechnology (C2112345C)
  4. Bachelor of Science Honours in Hospitality and Tourism Management (C2023456H)
  5. Bachelor of Science Honours in Supply Chain Management (C2134567S)
  6. Bachelor of Technology Honours in Creative Art & Industrial Design (C1945678D)
  7. Bachelor of Science Honours in Wildlife Ecology and Conservation (C2256789W)
  8. Bachelor of Science Honours in Agricultural Engineering (C2067890A)
  9. Bachelor of Science Honours in Environmental Engineering (C2178901E)
  10. Master of Technology in Advanced Manufacturing (C2389012M)

### 6. Bindura University of Science Education (BUSE)
- **Institution Code**: `BUSE-006`
- **Degrees**:
  1. Bachelor of Science Education Honours in Mathematics (B2046234M)
  2. Bachelor of Science Education Honours in Physics (B1945123P)
  3. Bachelor of Science Honours in Optometry (B2147345O)
  4. Bachelor of Science Honours in Computer Science (B2058456C)
  5. Bachelor of Science Honours in Natural Resources Management (B1969567N)
  6. Bachelor of Science Education Honours in Chemistry (B2170678CH)
  7. Bachelor of Science Honours in Sports Science and Management (B2081789S)
  8. Bachelor of Science Honours in Agricultural Economics (B2292890A)
  9. Bachelor of Science Honours in Statistics & Financial Mathematics (B2103901ST)
  10. Master of Science Education in Curriculum Studies (B2314012ED)

### 7. Great Zimbabwe University (GZU)
- **Institution Code**: `GZU-007`
- **Degrees**:
  1. Bachelor of Science Honours in Archeology, Museums & Heritage Studies (G2024567A)
  2. Bachelor of Commerce Honours in Business Management (G1923456B)
  3. Bachelor of Arts Honours in Development Studies (G2125678D)
  4. Bachelor of Science Honours in Agriculture and Natural Sciences (G2026789AG)
  5. Bachelor of Education Honours in Early Childhood Development (G1927890E)
  6. Bachelor of Science Honours in Information Technology (G2128901IT)
  7. Bachelor of Commerce Honours in Marketing (G2029012M)
  8. Bachelor of Science Honours in Sociology (G2230123S)
  9. Bachelor of Laws Honours (LLB) (G1831234L)
  10. Master of Science in Peace, Human Rights and Governance (G2332345P)

### 8. Harare Institute of Technology (HIT)
- **Institution Code**: `HIT-008`
- **Degrees**:
  1. Bachelor of Technology Honours in Software Engineering (H200234T)
  2. Bachelor of Technology Honours in Information Security and Assurance (H190123M)
  3. Bachelor of Technology Honours in Electronic Commerce (H210345W)
  4. Bachelor of Technology Honours in Chemical Technology (H200456C)
  5. Bachelor of Technology Honours in Biotechnology (H210567B)
  6. Bachelor of Technology Honours in Electronic Engineering (H190678E)
  7. Bachelor of Technology Honours in Materials Science & Technology (H220789M)
  8. Bachelor of Technology Honours in Biomedical Engineering (H200890BM)
  9. Bachelor of Technology Honours in Pharmaceutical Technology (H210901P)
  10. Master of Technology in Cloud Computing and IoT (H231012CC)

---

## Proposed Changes

### Configuration / Seeding

#### [MODIFY] [DataInitializer.java](file:///C:/Users/Madziva/.gemini/antigravity/scratch/qualification-verification-system/src/main/java/com/devops/qvs/config/DataInitializer.java)
- Expand seeding logic to register:
  - 8 Zimbabwe State Universities with active statuses, official domains, and contact emails.
  - Institutional officer accounts (`officer_zou`, `officer_uz`, `officer_nust`, etc.).
  - 80 authentic qualification records (10 per university) with realistic dates, classifications, digital fingerprints, and blockchain blocks.
  - ZOU records explicitly implementing the required `ITM 190020` format for IT degrees and `EH250001` (year-prefixed) format for PGDE degrees.
- Total qualifications seeded: 83 records (well above 70).

### Verification Service

#### [MODIFY] [VerificationService.java](file:///C:/Users/Madziva/.gemini/antigravity/scratch/qualification-verification-system/src/main/java/com/devops/qvs/service/VerificationService.java)
- Enhance lookup query fallback to check `findByStudentIdNumber` so verifiers can verify credentials by entering student ID directly (e.g. `ITM 190020` or `EH250001`) as well as certificate numbers or SHA-256 hashes.

### UI Templates

#### [MODIFY] [dashboard.html](file:///C:/Users/Madziva/.gemini/antigravity/scratch/qualification-verification-system/src/main/resources/templates/dashboard.html)
- Add quick demo test buttons for ZOU IT (`ITM 190020`), ZOU PGDE (`EH250001`), and other state university degrees.

---

## Verification Plan

### Automated Tests
- Run maven unit and integration tests:
  ```powershell
  .\mvnw.cmd test
  ```
- Ensure `VerificationControllerIntegrationTest`, `AuthControllerIntegrationTest`, and `QualificationServiceTest` pass completely.

### Verification of Seed Records
- Verify total qualification count is 83 (>= 70 records).
- Query `/api/v1/qualifications/search?q=ZOU` and `/api/v1/qualifications/search?q=ITM` and `/api/v1/qualifications/search?q=EH250001`.
- Verify ZOU IT degree `ITM 190020` and PGDE `EH250001` verify successfully with status `GENUINE_AND_VALID` and valid blockchain proof.
