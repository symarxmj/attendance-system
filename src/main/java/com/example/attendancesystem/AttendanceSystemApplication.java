package com.example.attendancesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
@SpringBootApplication
@MapperScan("com.example.attendancesystem.mapper")
public class AttendanceSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(AttendanceSystemApplication.class, args);

    }
}
