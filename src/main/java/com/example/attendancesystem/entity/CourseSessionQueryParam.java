package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class CourseSessionQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String courseId;
    private String sessionDate;
}
