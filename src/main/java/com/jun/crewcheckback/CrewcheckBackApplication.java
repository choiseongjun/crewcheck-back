package com.jun.crewcheckback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CrewcheckBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewcheckBackApplication.class, args);
    }

}
