package com.example.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
    private Integer id;
    private String classroomName;
    private Integer rows;
    private Integer cols;
    private String excludeSeats;
    private LocalDateTime createTime;
}
