package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSelection {
    private Long id;
    private String studentId;
    private String courseId;
    private LocalDateTime selectTime;
}
