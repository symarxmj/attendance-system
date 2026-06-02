package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSession {
    private Long sessionId;
    private String courseId;
    private String sessionDate;
    private Integer weekNumber;
    private Integer status;
}
