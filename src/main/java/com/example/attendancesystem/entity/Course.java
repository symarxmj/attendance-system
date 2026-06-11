package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String courseId;
    private String courseName;
    private Long teacherId;
    private Integer classroomId;
    private Integer weekday;
    private Integer startWeek;
    private Integer endWeek;
    private LocalDateTime createTime;
    private String teacherName;  // 仅用于列表展示，非持久化字段
}
