package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class CourseQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String courseId;
    private String courseName;
    private Long teacherId;
    private Integer classroomId;
}
