package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class ClassroomQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer id;
    private String classroomName;
}
