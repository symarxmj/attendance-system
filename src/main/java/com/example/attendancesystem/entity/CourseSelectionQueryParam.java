package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class CourseSelectionQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String studentId;
    private String courseId;
}
