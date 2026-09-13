package com.devops.qvs.config;

import com.devops.qvs.model.Institution;
import com.devops.qvs.model.Qualification;
import com.devops.qvs.model.QualificationStatus;
import com.devops.qvs.model.Role;
import com.devops.qvs.model.User;
import com.devops.qvs.repository.InstitutionRepository;
import com.devops.qvs.repository.QualificationRepository;
import com.devops.qvs.repository.UserRepository;
import com.devops.qvs.service.BlockchainLedgerService;
import com.devops.qvs.service.CryptoHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final QualificationRepository qualificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final CryptoHashService cryptoHashService;
    private final BlockchainLedgerService blockchainLedgerService;

    public DataInitializer(UserRepository userRepository,
                           InstitutionRepository institutionRepository,
                           QualificationRepository qualificationRepository,
                           PasswordEncoder passwordEncoder,
                           CryptoHashService cryptoHashService,
                           BlockchainLedgerService blockchainLedgerService) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.qualificationRepository = qualificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.cryptoHashService = cryptoHashService;
        this.blockchainLedgerService = blockchainLedgerService;
    }

    private record SeedRecord(
            String certNum,
            String studentName,
            String studentId,
            String awardTitle,
            String specialization,
            String classification,
            LocalDate awardDate,
            QualificationStatus status,
            String revocationReason
    ) {}

    @Override
    public void run(String... args) {
        if (institutionRepository.count() == 0) {
            logger.info("Initializing Comprehensive DevOps Qualification Data & Zimbabwe State Universities Ledger...");

            // 1. Core Users (Admin & Verifier)
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@qvs.internal")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .fullName("DevOps System Administrator")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .username("verifier")
                    .email("verifier@trusted-partner.org")
                    .password(passwordEncoder.encode("Verifier@12345"))
                    .fullName("Accredited Verification Agent")
                    .role(Role.ROLE_VERIFIER)
                    .enabled(true)
                    .build());

            // 2. Baseline Demo Institutions & Qualifications (preserves test suite stability)
            seedBaselineDemoInstitutions();

            // 3. Zimbabwe State Universities & Qualifications (10 students each with distinct degrees)
            seedZimbabweStateUniversities();

            logger.info("Qualification Verification System successfully seeded with {} institutions and {} qualifications on blockchain ledger.",
                    institutionRepository.count(), qualificationRepository.count());
        }
    }

    private void seedBaselineDemoInstitutions() {
        Institution inst1 = institutionRepository.save(Institution.builder()
                .name("National Institute of Technology")
                .institutionCode("NIT-001")
                .contactEmail("verify@nit.edu")
                .country("United Kingdom")
                .website("https://nit.edu")
                .build());

        Institution inst2 = institutionRepository.save(Institution.builder()
                .name("Metropolitan University")
                .institutionCode("METRO-002")
                .contactEmail("registry@metro.ac.uk")
                .country("United Kingdom")
                .website("https://metro.ac.uk")
                .build());

        userRepository.save(User.builder()
                .username("officer")
                .email("officer@nit.edu")
                .password(passwordEncoder.encode("Officer@12345"))
                .fullName("Academic Registrar Officer")
                .role(Role.ROLE_INSTITUTION)
                .institution(inst1)
                .enabled(true)
                .build());

        createSeedQualification(
                "QVS-2024-BSC-8891",
                "Sarah Jenkins",
                "STU-990142",
                "Bachelor of Science in Software Engineering",
                "DevOps & Cloud Computing",
                "First Class Honours",
                LocalDate.of(2024, 7, 15),
                inst1,
                QualificationStatus.ACTIVE,
                null
        );

        createSeedQualification(
                "QVS-2023-MSC-4412",
                "David Miller",
                "STU-881230",
                "Master of Science in Cybersecurity",
                "Information Security & Cryptography",
                "Distinction",
                LocalDate.of(2023, 11, 20),
                inst1,
                QualificationStatus.ACTIVE,
                null
        );

        createSeedQualification(
                "QVS-2022-DIP-1109",
                "Marcus Vance",
                "STU-772911",
                "Diploma in Enterprise Systems Administration",
                "Infrastructure Management",
                "Upper Second Class",
                LocalDate.of(2022, 6, 30),
                inst2,
                QualificationStatus.REVOKED,
                "Issued in error - Academic misconduct disciplinary ruling"
        );
    }

    private void seedZimbabweStateUniversities() {
        // =========================================================================
        // 1. Zimbabwe Open University (ZOU)
        // Specific requirement: IT degrees format ITM 190020, PGDE format EH250001 (first 2 digits = year)
        // =========================================================================
        Institution zou = seedInstitutionWithOfficer(
                "Zimbabwe Open University",
                "ZOU-004",
                "registry@zou.ac.zw",
                "https://www.zou.ac.zw",
                "officer_zou",
                "ZOU Academic Registrar Officer"
        );

        List<SeedRecord> zouRecords = List.of(
                // IT Degrees: format ITM 190020 etc.
                new SeedRecord("ZOU-2023-ITM-190020", "Itai Muringani", "ITM 190020",
                        "Bachelor of Science Honours in Information Technology", "Network Administration & Cloud Systems",
                        "First Class Honours", LocalDate.of(2023, 11, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2024-ITM-200021", "Rutendo Chitepo", "ITM 200021",
                        "Bachelor of Science Honours in Network Computing", "Cybersecurity & Systems Infrastructure",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 11, 22), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2025-ITM-210022", "Memory Mandaza", "ITM 210022",
                        "Bachelor of Science Honours in Software Engineering", "Enterprise Application Development",
                        "First Class Honours", LocalDate.of(2025, 5, 18), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2026-ITM-220023", "Kuda Marange", "ITM 220023",
                        "Bachelor of Science Honours in Multimedia Technology", "Digital Media & Interactive Web",
                        "Upper Second Class (2.1)", LocalDate.of(2026, 3, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2023-ITM-190024", "Learnmore Zimunya", "ITM 190024",
                        "Bachelor of Science Honours in Management Information Systems", "Enterprise Database Administration",
                        "Lower Second Class (2.2)", LocalDate.of(2023, 11, 24), QualificationStatus.ACTIVE, null),

                // PGDE Degrees: format EH250001 where the first 2 digits are the year
                new SeedRecord("ZOU-2025-PGDE-EH250001", "Sekai Mataranyika", "EH250001",
                        "Postgraduate Diploma in Education (Mathematics)", "Secondary Mathematics Pedagogy",
                        "Distinction", LocalDate.of(2025, 11, 28), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2024-PGDE-EH240002", "Tapiwa Rusike", "EH240002",
                        "Postgraduate Diploma in Education (Science)", "Integrated Sciences Pedagogy",
                        "Merit", LocalDate.of(2024, 11, 22), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2023-PGDE-EH230003", "Grace Mukaro", "EH230003",
                        "Postgraduate Diploma in Education (English Language)", "Applied Linguistics & Literature Pedagogy",
                        "Distinction", LocalDate.of(2023, 11, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2022-PGDE-EH220004", "Admire Charamba", "EH220004",
                        "Postgraduate Diploma in Education (Educational Leadership)", "School Administration & Supervision",
                        "Merit", LocalDate.of(2022, 11, 25), QualificationStatus.ACTIVE, null),
                new SeedRecord("ZOU-2021-PGDE-EH210005", "Patience Mutungwazi", "EH210005",
                        "Postgraduate Diploma in Education (ICT in Education)", "Instructional Media & E-Learning",
                        "Pass", LocalDate.of(2021, 11, 26), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(zou, zouRecords);

        // =========================================================================
        // 2. University of Zimbabwe (UZ)
        // =========================================================================
        Institution uz = seedInstitutionWithOfficer(
                "University of Zimbabwe",
                "UZ-001",
                "registrar@uz.ac.zw",
                "https://www.uz.ac.zw",
                "officer_uz",
                "UZ Academic Registrar Officer"
        );

        List<SeedRecord> uzRecords = List.of(
                new SeedRecord("UZ-2024-BSC-3112", "Tendai Moyo", "R203112B",
                        "Bachelor of Science Honours in Computer Science", "Artificial Intelligence & Distributed Systems",
                        "First Class Honours", LocalDate.of(2024, 7, 12), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2023-BSC-2034", "Chipo Dube", "R192034A",
                        "Bachelor of Science Honours in Civil Engineering", "Structural Engineering & Hydraulics",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 7, 14), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2024-MED-4512", "Farai Ncube", "R184512M",
                        "Bachelor of Medicine and Bachelor of Surgery (MBChB)", "General Medicine & Clinical Surgery",
                        "Honours", LocalDate.of(2024, 12, 5), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2025-LLB-4556", "Tatenda Sibanda", "R214556C",
                        "Bachelor of Laws Honours (LLBS)", "Commercial Law & Constitutional Jurisprudence",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 6, 20), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2024-BEE-1098", "Blessing Chikwanha", "R201098E",
                        "Bachelor of Science Honours in Electrical and Electronic Engineering", "Power Systems Automation",
                        "First Class Honours", LocalDate.of(2024, 7, 12), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2023-PHARM-5678", "Rufaro Sithole", "R195678P",
                        "Bachelor of Science Honours in Pharmacy", "Clinical Pharmacology & Therapeutics",
                        "Distinction", LocalDate.of(2023, 11, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2025-ECO-1098", "Kudakwashe Mutasa", "R221098D",
                        "Bachelor of Science Honours in Economics", "Econometrics & Quantitative Finance",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 7, 11), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2024-MINE-8765", "Tinashe Gumbo", "R208765M",
                        "Bachelor of Science Honours in Mining Engineering", "Geotechnical Mechanics & Mineral Processing",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 7, 12), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2025-AGR-2345", "Ruvimbo Ndlovu", "R212345A",
                        "Bachelor of Science Honours in Agriculture (Crop Science)", "Agronomy & Precision Crop Protection",
                        "First Class Honours", LocalDate.of(2025, 7, 11), QualificationStatus.ACTIVE, null),
                new SeedRecord("UZ-2025-MSC-9871", "Simbarashe Mpofu", "R239871S",
                        "Master of Science in Data Analytics", "Big Data Architectures & Deep Learning",
                        "Distinction", LocalDate.of(2025, 11, 21), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(uz, uzRecords);

        // =========================================================================
        // 3. National University of Science and Technology (NUST)
        // =========================================================================
        Institution nust = seedInstitutionWithOfficer(
                "National University of Science and Technology",
                "NUST-002",
                "admissions@nust.ac.zw",
                "https://www.nust.ac.zw",
                "officer_nust",
                "NUST Academic Registrar Officer"
        );

        List<SeedRecord> nustRecords = List.of(
                new SeedRecord("NUST-2024-MAT-5678", "Bongani Khumalo", "N0205678G",
                        "Bachelor of Science Honours in Applied Mathematics", "Numerical Analysis & Financial Modeling",
                        "First Class Honours", LocalDate.of(2024, 10, 18), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2023-CHE-1234", "Thandeka Nkomo", "N0191234F",
                        "Bachelor of Science Honours in Chemical Engineering", "Petrochemicals & Process Control Systems",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 20), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2025-CSC-4321", "Sibusiso Nyathi", "N0214321H",
                        "Bachelor of Science Honours in Computer Science", "Cybersecurity & Systems Programming",
                        "First Class Honours", LocalDate.of(2025, 10, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2024-ELE-9876", "Mthokozisi Tshuma", "N0209876E",
                        "Bachelor of Science Honours in Electronic Engineering", "Embedded Robotics & Telemetry",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 10, 18), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2025-BIO-3456", "Nomagugu Mlambo", "N0213456B",
                        "Bachelor of Science Honours in Applied Biology and Biochemistry", "Cellular Biology & Biotechnology",
                        "Distinction", LocalDate.of(2025, 10, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2023-IME-8765", "Vusumuzi Mathe", "N0198765M",
                        "Bachelor of Science Honours in Industrial & Manufacturing Engineering", "Additive Manufacturing & Lean Systems",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 20), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2024-ARC-7654", "Nqobile Bhebhe", "N0187654A",
                        "Bachelor of Science Honours in Architecture", "Sustainable Urban Design & Construction",
                        "First Class Honours", LocalDate.of(2024, 6, 15), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2025-ENV-8765", "Lindiwe Dlodlo", "N0228765K",
                        "Bachelor of Science Honours in Environmental Science and Health", "Environmental Ecology & Hazard Management",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2024-RAD-4321", "Zenzo Sibanda", "N0204321R",
                        "Bachelor of Science Honours in Radiography", "Diagnostic Medical Imaging & Ultrasound",
                        "Distinction", LocalDate.of(2024, 10, 18), QualificationStatus.ACTIVE, null),
                new SeedRecord("NUST-2025-MIS-1290", "Gugulethu Dube", "N0231290S",
                        "Master of Science in Information Systems", "Enterprise Architecture & Information Governance",
                        "Distinction", LocalDate.of(2025, 11, 28), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(nust, nustRecords);

        // =========================================================================
        // 4. Midlands State University (MSU)
        // =========================================================================
        Institution msu = seedInstitutionWithOfficer(
                "Midlands State University",
                "MSU-003",
                "registry@msu.ac.zw",
                "https://www.msu.ac.zw",
                "officer_msu",
                "MSU Academic Registrar Officer"
        );

        List<SeedRecord> msuRecords = List.of(
                new SeedRecord("MSU-2024-IS-2133", "Tafadzwa Chigumba", "R2021334M",
                        "Bachelor of Science Honours in Information Systems", "Database Engineering & Web Systems",
                        "First Class Honours", LocalDate.of(2024, 11, 15), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2023-ACC-1024", "Fungai Mavhunga", "R1910245P",
                        "Bachelor of Commerce Honours in Accounting", "Corporate Reporting & Forensic Audit",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2025-HR-3244", "Anesu Marufu", "R2132445K",
                        "Bachelor of Science Honours in Human Resource Management", "Strategic People Analytics & Labor Relations",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 5, 23), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2024-BF-4561", "Nyasha Dziva", "R2045612B",
                        "Bachelor of Commerce Honours in Banking and Finance", "Capital Markets & Risk Modeling",
                        "First Class Honours", LocalDate.of(2024, 11, 15), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2023-MSS-8765", "Tariro Mushore", "R1987654M",
                        "Bachelor of Science Honours in Media and Society Studies", "Digital Journalism & Public Communications",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2025-TEL-5678", "Chengetai Zhou", "R2156789T",
                        "Bachelor of Science Honours in Telecommunications", "Wireless Infrastructure & 5G Technologies",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 11, 21), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2024-FST-9876", "Tadiwa Shumba", "R2098765F",
                        "Bachelor of Science Honours in Food Science and Technology", "Food Safety Engineering & Agro-Processing",
                        "First Class Honours", LocalDate.of(2024, 11, 15), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2023-LLB-7654", "Panashe Mupfumi", "R1876543L",
                        "Bachelor of Laws Honours (LLB)", "Constitutional Jurisprudence & Corporate Litigation",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 17), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2025-PSY-4355", "Takudzwa Makoni", "R2243556T",
                        "Bachelor of Science Honours in Psychology", "Organizational Assessment & Behavioral Analysis",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 11, 21), QualificationStatus.ACTIVE, null),
                new SeedRecord("MSU-2025-SM-1209", "Munyaradzi Chuma", "R2312098S",
                        "Master of Commerce in Strategic Management", "Global Corporate Strategy & Leadership",
                        "Distinction", LocalDate.of(2025, 11, 21), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(msu, msuRecords);

        // =========================================================================
        // 5. Chinhoyi University of Technology (CUT)
        // =========================================================================
        Institution cut = seedInstitutionWithOfficer(
                "Chinhoyi University of Technology",
                "CUT-005",
                "academicregistry@cut.ac.zw",
                "https://www.cut.ac.zw",
                "officer_cut",
                "CUT Academic Registrar Officer"
        );

        List<SeedRecord> cutRecords = List.of(
                new SeedRecord("CUT-2024-PE-1123", "Tanaka Chiwenga", "C2011234B",
                        "Bachelor of Technology Honours in Production Engineering", "Automated Manufacturing & CNC Systems",
                        "First Class Honours", LocalDate.of(2024, 10, 25), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2023-MEC-1012", "Danai Mhere", "C1910123A",
                        "Bachelor of Science Honours in Mechatronics Engineering", "Industrial Robotics & Sensor Systems",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 27), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2025-BT-1234", "Vimbai Samukange", "C2112345C",
                        "Bachelor of Technology Honours in Biotechnology", "Industrial Microbiology & Bioprocess Engineering",
                        "Distinction", LocalDate.of(2025, 10, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2024-HTM-2345", "Courage Maphosa", "C2023456H",
                        "Bachelor of Science Honours in Hospitality and Tourism Management", "Resort Operations & Eco-Tourism Management",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 10, 25), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2025-SCM-3456", "Shamiso Munemo", "C2134567S",
                        "Bachelor of Science Honours in Supply Chain Management", "Global Logistics & Strategic Procurement",
                        "First Class Honours", LocalDate.of(2025, 10, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2023-ID-4567", "Gamuchirai Zvobgo", "C1945678D",
                        "Bachelor of Technology Honours in Creative Art & Industrial Design", "Ergonomic Product Prototyping",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 27), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2025-WEC-5678", "Innocent Hove", "C2256789W",
                        "Bachelor of Science Honours in Wildlife Ecology and Conservation", "Wildlife Sanctuary Management & Biodiversity",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2024-AGE-6789", "Ropafadzo Masawi", "C2067890A",
                        "Bachelor of Science Honours in Agricultural Engineering", "Irrigation Systems & Farm Power Technology",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 10, 25), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2025-ENE-7890", "Kelvin Mashonganyika", "C2178901E",
                        "Bachelor of Science Honours in Environmental Engineering", "Sanitation & Water Resource Engineering",
                        "First Class Honours", LocalDate.of(2025, 10, 24), QualificationStatus.ACTIVE, null),
                new SeedRecord("CUT-2025-MAM-8901", "Paidamoyo Choto", "C2389012M",
                        "Master of Technology in Advanced Manufacturing", "Computer-Aided Design & Smart Systems",
                        "Distinction", LocalDate.of(2025, 11, 28), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(cut, cutRecords);

        // =========================================================================
        // 6. Bindura University of Science Education (BUSE)
        // =========================================================================
        Institution buse = seedInstitutionWithOfficer(
                "Bindura University of Science Education",
                "BUSE-006",
                "admissions@buse.ac.zw",
                "https://www.buse.ac.zw",
                "officer_buse",
                "BUSE Academic Registrar Officer"
        );

        List<SeedRecord> buseRecords = List.of(
                new SeedRecord("BUSE-2024-BED-4623", "Prince Tagwirei", "B2046234M",
                        "Bachelor of Science Education Honours in Mathematics", "Secondary & Tertiary Mathematics Pedagogy",
                        "First Class Honours", LocalDate.of(2024, 11, 8), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2023-BED-4512", "Faith Munetsi", "B1945123P",
                        "Bachelor of Science Education Honours in Physics", "Experimental Physics & Science Education",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2025-OPT-4734", "Nigel Chifamba", "B2147345O",
                        "Bachelor of Science Honours in Optometry", "Clinical Optometry & Visual Optics",
                        "Distinction", LocalDate.of(2025, 11, 7), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2024-CSC-5845", "Tsitsi Musonza", "B2058456C",
                        "Bachelor of Science Honours in Computer Science", "Data Science & Cloud Architectures",
                        "First Class Honours", LocalDate.of(2024, 11, 8), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2023-NRM-6956", "Delight Madondo", "B1969567N",
                        "Bachelor of Science Honours in Natural Resources Management", "Forestry Protection & Watershed Ecosystems",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2025-BED-7067", "Prosper Machingura", "B2170678CH",
                        "Bachelor of Science Education Honours in Chemistry", "Inorganic & Organic Chemistry Pedagogy",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 11, 7), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2024-SSM-8178", "Shalom Gwaze", "B2081789S",
                        "Bachelor of Science Honours in Sports Science and Management", "Athletic Physiology & Sports Administration",
                        "First Class Honours", LocalDate.of(2024, 11, 8), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2025-AEC-9289", "Tichaona Mukamuri", "B2292890A",
                        "Bachelor of Science Honours in Agricultural Economics", "Agri-Enterprise Finance & Rural Development",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 11, 7), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2025-SFM-0390", "Marvelous Muchena", "B2103901ST",
                        "Bachelor of Science Honours in Statistics & Financial Mathematics", "Actuarial Science & Stochastic Analytics",
                        "Distinction", LocalDate.of(2025, 11, 7), QualificationStatus.ACTIVE, null),
                new SeedRecord("BUSE-2025-MED-1401", "Progress Chirwa", "B2314012ED",
                        "Master of Science Education in Curriculum Studies", "Pedagogical Policy & Digital Instructional Systems",
                        "Distinction", LocalDate.of(2025, 11, 21), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(buse, buseRecords);

        // =========================================================================
        // 7. Great Zimbabwe University (GZU)
        // =========================================================================
        Institution gzu = seedInstitutionWithOfficer(
                "Great Zimbabwe University",
                "GZU-007",
                "admissions@gzu.ac.zw",
                "https://www.gzu.ac.zw",
                "officer_gzu",
                "GZU Academic Registrar Officer"
        );

        List<SeedRecord> gzuRecords = List.of(
                new SeedRecord("GZU-2024-AMH-2456", "Webster Nyikadzino", "G2024567A",
                        "Bachelor of Science Honours in Archeology, Museums & Heritage Studies", "Heritage Conservation & Cultural Resource Management",
                        "First Class Honours", LocalDate.of(2024, 10, 11), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2023-BM-2345", "Tariro Matanhire", "G1923456B",
                        "Bachelor of Commerce Honours in Business Management", "Enterprise Strategy & Commercial Operations",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 13), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2025-DS-2567", "Donald Chinyama", "G2125678D",
                        "Bachelor of Arts Honours in Development Studies", "Community Development & Policy Planning",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2024-ANS-2678", "Mitchell Chimedza", "G2026789AG",
                        "Bachelor of Science Honours in Agriculture and Natural Sciences", "Dryland Agriculture & Sustainable Farming",
                        "First Class Honours", LocalDate.of(2024, 10, 11), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2023-ECD-2789", "Brian Mudavanhu", "G1927890E",
                        "Bachelor of Education Honours in Early Childhood Development", "Early Childhood Pedagogical Systems",
                        "Distinction", LocalDate.of(2023, 10, 13), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2025-IT-2890", "Evidence Chiduku", "G2128901IT",
                        "Bachelor of Science Honours in Information Technology", "Network Administration & Data Assurance",
                        "First Class Honours", LocalDate.of(2025, 10, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2024-MKT-2901", "Lorraine Bangira", "G2029012M",
                        "Bachelor of Commerce Honours in Marketing", "Digital Branding & Consumer Insights",
                        "Upper Second Class (2.1)", LocalDate.of(2024, 10, 11), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2025-SOC-3012", "Godknows Mtetwa", "G2230123S",
                        "Bachelor of Science Honours in Sociology", "Social Dynamics & Community Research",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 10), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2023-LLB-3123", "Samantha Murenje", "G1831234L",
                        "Bachelor of Laws Honours (LLB)", "Constitutional Jurisprudence & Civil Litigation",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 10, 13), QualificationStatus.ACTIVE, null),
                new SeedRecord("GZU-2025-MS-3234", "Never Chivasa", "G2332345P",
                        "Master of Science in Peace, Human Rights and Governance", "Conflict Transformation & International Governance",
                        "Distinction", LocalDate.of(2025, 11, 14), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(gzu, gzuRecords);

        // =========================================================================
        // 8. Harare Institute of Technology (HIT)
        // =========================================================================
        Institution hit = seedInstitutionWithOfficer(
                "Harare Institute of Technology",
                "HIT-008",
                "registry@hit.ac.zw",
                "https://www.hit.ac.zw",
                "officer_hit",
                "HIT Academic Registrar Officer"
        );

        List<SeedRecord> hitRecords = List.of(
                new SeedRecord("HIT-2024-BSE-0234", "Craig Mupanduki", "H200234T",
                        "Bachelor of Technology Honours in Software Engineering", "Cloud Computing & Distributed Microservices",
                        "First Class Honours", LocalDate.of(2024, 11, 1), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2023-ISA-0123", "Natasha Gwenzi", "H190123M",
                        "Bachelor of Technology Honours in Information Security and Assurance", "Ethical Hacking & Network Defense",
                        "First Class Honours", LocalDate.of(2023, 11, 3), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2025-ECO-0345", "Keith Zinyemba", "H210345W",
                        "Bachelor of Technology Honours in Electronic Commerce", "Fintech Innovations & Digital Payment Gateways",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 31), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2024-CT-0456", "Nicole Matema", "H200456C",
                        "Bachelor of Technology Honours in Chemical Technology", "Polymer Chemistry & Industrial Synthesis",
                        "Distinction", LocalDate.of(2024, 11, 1), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2025-BIO-0567", "Arnold Chimutashu", "H210567B",
                        "Bachelor of Technology Honours in Biotechnology", "Bioprocess Engineering & Genetic Sequencing",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 31), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2023-ELE-0678", "Prudence Manyika", "H190678E",
                        "Bachelor of Technology Honours in Electronic Engineering", "Embedded Hardware & Telemetry Systems",
                        "Upper Second Class (2.1)", LocalDate.of(2023, 11, 3), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2025-MST-0789", "Panashe Chibwe", "H220789M",
                        "Bachelor of Technology Honours in Materials Science & Technology", "Nanomaterials & Metallurgical Analysis",
                        "First Class Honours", LocalDate.of(2025, 10, 31), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2024-BME-0890", "Tanyaradzwa Mudimu", "H200890BM",
                        "Bachelor of Technology Honours in Biomedical Engineering", "Medical Device Engineering & Telemedicine",
                        "Distinction", LocalDate.of(2024, 11, 1), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2025-PHT-0901", "Anotida Chidawanyika", "H210901P",
                        "Bachelor of Technology Honours in Pharmaceutical Technology", "Industrial Drug Formulation & Packaging",
                        "Upper Second Class (2.1)", LocalDate.of(2025, 10, 31), QualificationStatus.ACTIVE, null),
                new SeedRecord("HIT-2025-MCC-1012", "Brandon Mutero", "H231012CC",
                        "Master of Technology in Cloud Computing and IoT", "Enterprise Kubernetes & Autonomous Edge Systems",
                        "Distinction", LocalDate.of(2025, 11, 28), QualificationStatus.ACTIVE, null)
        );
        batchSaveQualifications(hit, hitRecords);
    }

    private Institution seedInstitutionWithOfficer(String name, String code, String email, String website,
                                                    String officerUsername, String officerFullName) {
        Institution institution = institutionRepository.save(Institution.builder()
                .name(name)
                .institutionCode(code)
                .contactEmail(email)
                .country("Zimbabwe")
                .website(website)
                .active(true)
                .build());

        userRepository.save(User.builder()
                .username(officerUsername)
                .email(email)
                .password(passwordEncoder.encode("Officer@12345"))
                .fullName(officerFullName)
                .role(Role.ROLE_INSTITUTION)
                .institution(institution)
                .enabled(true)
                .build());

        return institution;
    }

    private void batchSaveQualifications(Institution institution, List<SeedRecord> records) {
        for (SeedRecord r : records) {
            createSeedQualification(
                    r.certNum(),
                    r.studentName(),
                    r.studentId(),
                    r.awardTitle(),
                    r.specialization(),
                    r.classification(),
                    r.awardDate(),
                    institution,
                    r.status(),
                    r.revocationReason()
            );
        }
    }

    private void createSeedQualification(String certNum, String studentName, String studentId,
                                         String awardTitle, String specialization, String classification,
                                         LocalDate awardDate, Institution institution, QualificationStatus status,
                                         String revocationReason) {
        String fingerprint = cryptoHashService.generateFingerprint(
                certNum, studentName, studentId, awardTitle, institution.getInstitutionCode(), awardDate
        );

        Qualification q = Qualification.builder()
                .certificateNumber(certNum)
                .studentFullName(studentName)
                .studentIdNumber(studentId)
                .awardTitle(awardTitle)
                .majorSpecialization(specialization)
                .classification(classification)
                .awardDate(awardDate)
                .institution(institution)
                .status(status)
                .digitalFingerprint(fingerprint)
                .revocationReason(revocationReason)
                .build();

        Qualification saved = qualificationRepository.save(q);

        try {
            blockchainLedgerService.mineBlock(
                    saved.getCertificateNumber(),
                    saved.getDigitalFingerprint(),
                    saved.getStatus() == QualificationStatus.REVOKED ? "REVOKE" : "ISSUE",
                    institution.getName()
            );
        } catch (Exception e) {
            logger.warn("Seeding blockchain block error: {}", e.getMessage());
        }
    }
}
