package com.demo.demotask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Demo Task Application.
 *
 * @author Danylo Kovalchuk
 */
@EnableScheduling
@EnableFeignClients
@EnableMongoAuditing
@SpringBootApplication
public class DemoTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoTaskApplication.class, args);
    }
}
