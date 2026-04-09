package com.example.attendancesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@MapperScan("com.example.attendancesystem.dao")
@RestController
public class AttendanceSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(AttendanceSystemApplication.class, args);

    }
}
