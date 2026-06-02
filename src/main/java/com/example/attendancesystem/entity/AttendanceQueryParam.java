package com.example.attendancesystem.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AttendanceQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Long sessionId;
    private String studentId;
    private String status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime end;
}
