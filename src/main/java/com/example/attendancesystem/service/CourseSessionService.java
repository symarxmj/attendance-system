package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.CourseSessionQueryParam;
import com.example.attendancesystem.util.PageResult;

import java.util.List;

public interface CourseSessionService {
    List<CourseSession> getTodaySessionsForStudent(String studentId);
    void insert(CourseSession session);
    void delete(Long sessionId);
    void update(CourseSession session);
    CourseSession findById(Long sessionId);
    PageResult<CourseSession> page(CourseSessionQueryParam param);
}
