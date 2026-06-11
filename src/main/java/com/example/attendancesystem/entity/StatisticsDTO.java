package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class StatisticsDTO {
    private long totalCount;
    private long presentCount;
    private long absentCount;
    private long normalCount;
    private long lateCount;
    private double attendanceRate;
}
