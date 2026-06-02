package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.CourseQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {
    Course findById(String courseId);
    List<Course> findAll();
    void insert(Course course);
    void delete(String courseId);
    void update(Course course);
    List<Course> list(CourseQueryParam param);
}
