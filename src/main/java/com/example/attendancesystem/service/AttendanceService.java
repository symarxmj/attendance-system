package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.AttendanceQueryParam;
import com.example.attendancesystem.entity.StatisticsDTO;
import com.example.attendancesystem.util.PageResult;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    String checkIn(Long sessionId, String studentId, String ip);
    Map<Long, Attendance> getTodayAttendanceMap(String studentId, List<Long> sessionIds);
    void delete(Long id);
    void updateStatus(Long id, String status);
    Attendance findById(Long id);
    PageResult<Attendance> page(AttendanceQueryParam param);

    StatisticsDTO getStudentStatistics(String studentId);

    void deleteByStudentId(String studentId);
}
