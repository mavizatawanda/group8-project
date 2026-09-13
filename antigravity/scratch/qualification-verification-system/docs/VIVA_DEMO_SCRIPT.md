# VIVA & DEMONSTRATION SCRIPT (10–15 Minutes Presentation)

**Project:** DevOps-Enabled Qualification Verification System (QVS)  
**Target Duration:** 12–15 Minutes  
**Audience:** Examiners, Academic Evaluators, and DevOps Assessors  

---

## ⏱ Time Allocation & Agenda Breakdown

| Section | Duration | Key Focus Area | Presenter(s) |
| :--- | :--- | :--- | :--- |
| **1. Introduction & Problem Context** | 2 mins | Credential fraud problem, project scope, architecture overview | Speaker 1 |
| **2. Git Workflow & Collaboration** | 2.5 mins | GitFlow branching, PR reviews, merge conflict prevention, commit history | Speaker 2 |
| **3. CI/CD Pipeline & Quality Gates** | 3.5 mins | GitHub Actions, Checkstyle, automated JUnit tests, JaCoCo coverage | Speaker 3 |
| **4. Live System Demonstration** | 4.5 mins | Public verification, tamper detection, qualification issuance, audit trail | Speaker 4 / All |
| **5. Reflection, Q&A, and Conclusions** | 2.5 mins | Lessons learnt, security considerations, limitations & future work | All Members |

---

## 🎬 Step-by-Step Viva Presentation Script

### Part 1: Introduction & Problem Context (0:00 - 2:00)
- **Visuals:** Title Slide -> System Architecture Diagram.
- **Speaking Notes:**
  > *"Good morning, esteemed assessors. Today, our team is presenting the DevOps-Enabled Qualification Verification System. Academic and professional credential fraud poses severe reputational and financial risks worldwide. Our goal was to engineer a cloud-native, secure, and verifiable platform backed by modern DevOps practices.*
  > *Our architecture utilizes Java 17 and Spring Boot 3 with a clean 4-tier layered design, containerized with Docker, and secured via SHA-256 cryptographic fingerprinting and JWT role-based access control."*

---

### Part 2: Git-Based Collaboration & Branching Strategy (2:00 - 4:30)
- **Visuals:** GitHub Repository insights, Network graph, PR list.
- **Speaking Notes:**
  > *"To ensure collaborative rigor across our team of engineers, we instituted a strict GitFlow branching model. All work occurred in isolated `feature/*` branches linked to GitHub issues.*
  > *Pull Requests required at least one peer code review, adherence to our PR checklist, and automated CI pipeline sign-off before merging into `develop` and `main`. This eliminated merge conflicts and preserved a clean, traceable commit history."*

---

### Part 3: DevOps CI/CD Pipeline & Quality Assurance (4:30 - 8:00)
- **Visuals:** GitHub Actions Pipeline run, JaCoCo Coverage Report, Checkstyle terminal output.
- **Speaking Notes:**
  > *"Our Continuous Integration pipeline in GitHub Actions runs four distinct jobs upon every push and PR:*
  > 1. *First, Static Code Analysis enforces Google Java style rules via Checkstyle.*
  > 2. *Second, Automated Testing runs our JUnit 5 unit tests and MockMvc integration tests. A JaCoCo quality gate enforces a strict 80% code coverage threshold, automatically failing the build if untested regressions occur.*
  > 3. *Third, Packaging generates the executable Spring Boot artifact.*
  > 4. *Finally, Dockerization executes a multi-stage Docker build, producing an unprivileged, minimal JRE runtime image for secure deployment."*

---

### Part 4: Live System Demonstration (8:00 - 12:30)
- **Visuals:** Live Web Portal at `http://localhost:8080` & Swagger UI.
- **Step 1: Public Verification of Authentic Credential**
  - Enter certificate `QVS-2024-BSC-8891` (Sarah Jenkins).
  - Click **Verify Authenticity**.
  - Show the **GENUINE & VALID** green badge and the matching SHA-256 cryptographic signature.
- **Step 2: Verification of Revoked Credential**
  - Enter certificate `QVS-2022-DIP-1109` (Marcus Vance).
  - Show the **REVOKED CREDENTIAL** red badge and display the disciplinary audit reason.
- **Step 3: Tamper-Resistance Demonstration (Bonus Feature)**
  - Explain how modifying any student record attribute causes an immediate cryptographic hash mismatch, flagging the record as **TAMPERED / SUSPICIOUS**.
- **Step 4: Registrar Officer Issuing a Credential**
  - Log in as `officer` (`Officer@12345`).
  - Navigate to **Institution Portal** (`/dashboard`).
  - Register a new qualification; show immediate calculation of the SHA-256 digital fingerprint.
- **Step 5: Inspecting the Audit Trail**
  - Navigate to **Audit Trail** (`/audit`).
  - Show real-time entries capturing the verification requests, inquiring IP address, timestamp, and verification outcome.

---

### Part 5: Critical Evaluation & Conclusion (12:30 - 15:00)
- **Visuals:** Summary slide with key metrics (test coverage %, container size, build time).
- **Speaking Notes:**
  > *"In conclusion, by embedding DevOps practices into every phase of development, our team delivered a robust, highly testable, and production-ready system. Key lessons learnt include the importance of automated quality gates and container security hardening.*
  > *Thank you, and we now welcome any questions."*
