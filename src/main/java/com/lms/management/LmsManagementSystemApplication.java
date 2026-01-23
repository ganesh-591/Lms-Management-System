package com.lms.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LmsManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsManagementSystemApplication.class, args);
    }
}
