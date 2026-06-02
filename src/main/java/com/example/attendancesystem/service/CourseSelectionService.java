package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSelectionQueryParam;
import com.example.attendancesystem.util.PageResult;

import java.util.List;

public interface CourseSelectionService {
    List<CourseSelection> findByStudentId(String studentId);
    void insert(CourseSelection cs);
    void delete(Long id);
    void update(CourseSelection cs);
    CourseSelection findById(Long id);
    PageResult<CourseSelection> page(CourseSelectionQueryParam param);
}
