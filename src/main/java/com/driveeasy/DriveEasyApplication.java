package com.driveeasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.driveeasy.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class DriveEasyApplication {

    public static void main(String[] args) {

        SpringApplication.run(DriveEasyApplication.class, args);
    }
}

