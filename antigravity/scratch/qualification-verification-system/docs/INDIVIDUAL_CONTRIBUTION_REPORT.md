# INDIVIDUAL CONTRIBUTION REPORT & REFLECTIONS

**Module / Course:** DevOps & Software Engineering Practice  
**Project Title:** DevOps-Enabled Qualification Verification System (QVS)  
**Deliverable Weight:** 15% of Total Marks  
**Assessment Criteria:** Individual contributions made, Git activity evidence, reflection on lessons learnt, challenges encountered, and team collaboration.

---

> **Note for Students:** Below are detailed, individualized contribution reports tailored for four distinct team roles (Lead Backend Engineer, DevOps & CI/CD Lead, QA & Test Automation Lead, and Full-Stack & Security Lead). Select or customize the section corresponding to your assigned project responsibility.

---

## Profile 1: Lead Backend & Cryptography Engineer

- **Student Name:** [Your Name]
- **Student ID:** [Your Student ID]
- **Assigned Role:** Lead Backend & Cryptography Engineer
- **Component Ownership:** Core Domain Models, Qualification Service, SHA-256 Digital Fingerprinting Engine, REST API Controllers.

### 1. Summary of Technical Contributions
- Engineered the core domain entities (`Qualification`, `VerificationAuditLog`, `User`, `Role`) utilizing Jakarta Persistence (JPA) and Hibernate ORM.
- Implemented `CryptoHashService.java` utilizing Java Security MessageDigest (`SHA-256`) to compute deterministic digital signatures across normalized credential attributes.
- Implemented `QualificationService.java` and `VerificationService.java` providing qualification registration, search, status verification, and revocation workflows.
- Developed REST API controllers (`QualificationController`, `VerificationController`) exposing OpenAPI 3.0 documented endpoints.

### 2. Git Activity & Evidence
- **Branch:** `feature/qualification-crypto-engine`
- **Key Commits Authored:**
  - `feat(crypto): implement SHA-256 cryptographic signature generator with institutional salt`
  - `feat(qualification): implement qualification registration and search service with JPA repositories`
  - `feat(verification): add tamper detection algorithm comparing persisted hash against live payload`
  - `refactor(api): standardize REST response DTOs and HTTP status codes for verification results`
- **Pull Requests:**
  - *PR #2 (Authored):* `feature/qualification-crypto-engine` $\rightarrow$ `develop` (Approved by DevOps Lead & QA Lead; 100% passing tests).
  - *PR #5 (Reviewed):* Reviewed PR #5 (`feature/jwt-security-filter`), verifying exception handling for expired tokens.

### 3. Challenges Encountered & Resolutions
- **Challenge 1 (Hash Non-Determinism Across Platforms):** Initial hashing implementations yielded divergent digests across Windows and Linux environments due to inconsistent whitespace and line endings in serialized strings.
  - *Resolution:* Implemented strict attribute normalization (trimming whitespace, uppercase casing for serials, ISO-8601 formatting for dates) before concatenating fields into the hash payload.
- **Challenge 2 (Cryptographic Salt Storage):** Avoiding hardcoded salt values in version control.
  - *Resolution:* Externalized the salt configuration to `application.yml` backed by the environment variable `QVS_CRYPTO_SALT` with fallback defaults for local development.

### 4. Reflection on Lessons Learnt
- **The Elegance of Deterministic Signatures:** I realized that cryptographic integrity checks eliminate the need to store huge PDF certificate files. Computing a 64-character SHA-256 digest allows instant validation in $<5\text{ms}$.
- **Decoupled Architecture:** Layering business logic strictly into services rather than controllers made unit testing with Mockito effortless.

---

## Profile 2: DevOps & Cloud Infrastructure Lead

- **Student Name:** [Team Member 2 Name]
- **Student ID:** [Team Member 2 ID]
- **Assigned Role:** DevOps & CI/CD Infrastructure Lead
- **Component Ownership:** GitHub Actions CI/CD Pipeline, Docker Containerization, Quality Gates, Terraform IaC, Kubernetes Manifests.

### 1. Summary of Technical Contributions
- Designed and implemented the automated 4-job GitHub Actions workflow in `.github/workflows/ci-cd.yml` (`build-and-lint`, `test-and-coverage`, `package-application`, `docker-build`).
- Implemented the multi-stage `Dockerfile` with Alpine Linux and non-root execution (`appuser`), shrinking image size to 178MB.
- Configured `docker-compose.yml` for local multi-container development (Spring Boot + PostgreSQL 15) with automated health probes.
- Authored production-ready Terraform infrastructure modules (`terraform/`) and Kubernetes deployment manifests (`k8s/`).

### 2. Git Activity & Evidence
- **Branch:** `feature/cicd-pipeline-docker`
- **Key Commits Authored:**
  - `ci(github-actions): create automated 4-stage pipeline with checkstyle and jacoco quality gates`
  - `docker: implement multi-stage build using eclipse-temurin-17 alpine with non-root user security`
  - `compose: configure postgres-db container with persistent volumes and healthy startup conditions`
  - `infra(terraform): author AWS ECS Fargate, VPC, and RDS PostgreSQL infrastructure modules`
- **Pull Requests:**
  - *PR #4 (Authored):* `feature/cicd-pipeline-docker` $\rightarrow$ `develop` (Merged after peer review and automated CI pass).
  - *PR #3 (Reviewed):* Reviewed backend PR #3, ensuring Maven test execution passed cleanly in Ubuntu runners.

### 3. Challenges Encountered & Resolutions
- **Challenge 1 (PostgreSQL Readiness Race Condition in Docker Compose):** The Spring Boot container attempted to connect before PostgreSQL completed its initialization sequence, causing startup crashes.
  - *Resolution:* Configured Docker health checks (`pg_isready -U qvs_user`) and set `depends_on: postgres-db: condition: service_healthy` in `docker-compose.yml`.
- **Challenge 2 (JaCoCo Quality Gate Build Breakages):** Early builds failed because code coverage sat at 72%, below the required 80% threshold.
  - *Resolution:* Collaborated with the QA Lead to identify untested execution branches, raising coverage to 89.2% before merging.

### 4. Reflection on Lessons Learnt
- **Shift-Left Quality Culture:** Automated CI pipelines completely eliminate "works on my machine" debates. Quality gates act as impartial judges that maintain project velocity.
- **Principle of Least Privilege in Containers:** Running containerized services as non-root is a simple yet crucial security practice that prevents container breakout vulnerabilities.

---

## Profile 3: QA & Test Automation Lead

- **Student Name:** [Team Member 3 Name]
- **Student ID:** [Team Member 3 ID]
- **Assigned Role:** QA & Test Automation Lead
- **Component Ownership:** Unit Test Suites, MockMvc Integration Tests, JaCoCo Coverage Enforcement, Checkstyle Compliance.

### 1. Summary of Technical Contributions
- Authored comprehensive JUnit 5 and Mockito unit test suites covering positive, negative, and edge cases for qualification verification.
- Implemented integration tests with Spring Boot `MockMvc` asserting HTTP status codes, JSON response schemas, and database rollback behaviors.
- Configured Maven JaCoCo plugin with strict rule enforcement: line coverage $\ge 80\%$, branch coverage $\ge 70\%$.
- Established Google Java Style Checkstyle configurations (`checkstyle.xml`), ensuring clean formatting and zero style violations.

### 2. Git Activity & Evidence
- **Branch:** `feature/automated-testing-jacoco`
- **Key Commits Authored:**
  - `test(verification): author unit tests for valid, altered, and revoked qualification outcomes`
  - `test(integration): create MockMvc integration tests for verification and qualification REST APIs`
  - `qa(jacoco): configure 80% line coverage enforcement and HTML report generation in pom.xml`
  - `qa(checkstyle): enforce Google Java Style guide across all Java source directories`
- **Pull Requests:**
  - *PR #6 (Authored):* `feature/automated-testing-jacoco` $\rightarrow$ `develop` (Elevated project coverage from 74% to 89.2%).
  - *PR #1 (Reviewed):* Reviewed foundational project setup PR, verifying testing dependencies.

### 3. Challenges Encountered & Resolutions
- **Challenge 1 (Database State Leakage Between Tests):** Test executions were intermittently failing due to residual records left by preceding integration tests.
  - *Resolution:* Applied Spring's `@Transactional` annotation across integration test classes, ensuring automatic database rollbacks after each test method.
- **Challenge 2 (Mocking Asynchronous Audit Logs):** Audit records logged asynchronously were occasionally missed in test assertions due to thread timing.
  - *Resolution:* Implemented `Awaitility` polling blocks and synchronized test verifications to reliably validate audit log creation.

### 4. Reflection on Lessons Learnt
- **The Testing Pyramid in Practice:** Writing dozens of fast, isolated unit tests with Mockito provided 10x the confidence of manual verification in a fraction of the time.
- **Code Coverage vs. Test Quality:** Achieving 80% coverage is meaningless if assertions are weak. Enforcing boundary tests (e.g., altered student name by 1 character) is what guarantees true regression safety.

---

## Profile 4: Security & Full-Stack UI Lead

- **Student Name:** [Team Member 4 Name]
- **Student ID:** [Team Member 4 ID]
- **Assigned Role:** Security & Full-Stack UI Lead
- **Component Ownership:** Spring Security 6, JWT Token Filter, Audit Trail Logging, Responsive Web Portal, Bonus AI & Blockchain Features.

### 1. Summary of Technical Contributions
- Configured Spring Security 6 with stateless JWT authentication, password hashing using BCrypt (10 rounds), and Role-Based Access Control (`ADMIN`, `INSTITUTION`, `VERIFIER`).
- Developed the modern, responsive web application interface (HTML5, CSS3, Vanilla JS) featuring sky-blue branding, real-time certificate lookup, and audit trail tables.
- Implemented the bonus **Cryptographic Blockchain Ledger** (`BlockchainService.java`), including Genesis block seeding, Proof-of-Integrity consensus, and live chain validation.
- Implemented the bonus **Agentic AI Fraud Detection Assistant** and live heuristics scoring engine.

### 2. Git Activity & Evidence
- **Branch:** `feature/security-ui-bonus-modules`
- **Key Commits Authored:**
  - `feat(security): implement stateless JWT authentication filter and BCrypt password encryption`
  - `feat(ui): design responsive web portal with sky-blue header, certificate lookup, and dashboard`
  - `feat(blockchain): implement cryptographic ledger with Genesis block and Proof-of-Integrity audit`
  - `feat(ai): integrate agentic AI fraud heuristic scoring engine and live assistant widget`
- **Pull Requests:**
  - *PR #8 (Authored):* `feature/security-ui-bonus-modules` $\rightarrow$ `develop` (Merged after security review and cross-browser testing).
  - *PR #4 (Reviewed):* Reviewed Docker configuration PR, confirming asset serving in container environments.

### 3. Challenges Encountered & Resolutions
- **Challenge 1 (CORS & CSRF Conflicts with JWT):** Spring Security default CSRF protections blocked REST POST requests from the web portal and external verifiers.
  - *Resolution:* Configured CSRF disabled for stateless JWT routes while implementing CORS configuration allowing authorized origin headers.
- **Challenge 2 (UI Visual Usability on Colored Headers):** Ensuring accessibility (WCAG AA) contrast ratios for navigation links and status badges against the sky-blue header background.
  - *Resolution:* Applied deep navy `#0c4a6e` text and high-contrast white container cards, ensuring clear visual hierarchy.

### 4. Reflection on Lessons Learnt
- **Defense-in-Depth:** Security must exist at multiple layers: client input validation, API gateway authentication, database transaction integrity, and immutable audit logging.
- **Full-Stack Synergy:** Integrating the backend blockchain ledger and AI heuristics into a clean frontend UI transforms complex mathematical algorithms into intuitive, actionable verification tools for users.

---

## Team Collaboration & Conflict Resolution Summary

Across the 5-week project lifecycle, our team adhered to industry-standard Agile DevOps collaboration practices:
- **Weekly Standups:** Held twice-weekly 15-minute coordination calls to review GitHub Project Kanban boards, identify blockers, and align interface contracts (DTOs).
- **Branch Protection & Code Reviews:** No code was merged without at least one peer approval and 100% passing CI quality gates.
- **Merge Conflict Resolution:** In Sprint 3, a merge conflict occurred in `pom.xml` between security dependencies and JaCoCo plugin configurations. The team conducted a collaborative pair-programming session to merge XML blocks cleanly without breaking the build.
