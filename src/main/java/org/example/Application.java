package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.example.controller.CourseController;
import org.example.controller.PingController;
import org.example.controller.S3Controller;
import org.example.service.CourseService;
import org.example.service.S3Service;
import org.example.service.SqsService;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "org.example.controller")
@Import({ PingController.class, CourseController.class, CourseService.class, S3Controller.class, S3Service.class, SqsService.class })
public class Application {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}