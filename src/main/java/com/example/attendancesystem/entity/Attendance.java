package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    private Long id;
    private Long sessionId;
    private String studentId;
    private LocalDateTime checkInTime;
    private Integer seatRow;
    private Integer seatCol;
    private String status;
    private String ip;
    private LocalDateTime createTime;

    // 非持久化字段，用于 JOIN 展示
    private String courseName;
    private String studentName;
    private String courseId;
    private LocalDateTime sessionDate;
}
