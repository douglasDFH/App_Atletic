package com.club.atletismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtletismoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtletismoApplication.class, args);
    }
}
