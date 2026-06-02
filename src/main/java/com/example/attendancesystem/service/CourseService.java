package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.CourseQueryParam;
import com.example.attendancesystem.util.PageResult;

import java.util.List;

public interface CourseService {
    void insert(Course course);
    void delete(String courseId);
    void update(Course course);
    Course findById(String courseId);
    List<Course> findAll();
    PageResult<Course> page(CourseQueryParam param);
}
