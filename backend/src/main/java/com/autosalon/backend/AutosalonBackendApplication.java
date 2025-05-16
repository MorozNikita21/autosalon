package com.autosalon.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class AutosalonBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutosalonBackendApplication.class, args);
    }

}
