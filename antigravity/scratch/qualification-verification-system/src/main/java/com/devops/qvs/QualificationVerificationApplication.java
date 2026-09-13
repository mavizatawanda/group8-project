package com.devops.qvs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QualificationVerificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualificationVerificationApplication.class, args);
    }
}
