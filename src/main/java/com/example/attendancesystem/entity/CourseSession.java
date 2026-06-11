package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSession {
    private Long sessionId;
    private String courseId;
    private LocalDateTime sessionDate;
    private Integer weekNumber;
    private Integer status;
    private String courseName;  // 仅用于列表展示，非持久化字段
}
