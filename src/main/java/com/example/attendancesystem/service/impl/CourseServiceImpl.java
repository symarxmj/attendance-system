package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.CourseQueryParam;
import com.example.attendancesystem.mapper.CourseMapper;
import com.example.attendancesystem.service.CourseService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public void insert(Course course) {
        course.setCreateTime(LocalDateTime.now());
        courseMapper.insert(course);
    }

    @Override
    public void delete(String courseId) {
        courseMapper.delete(courseId);
    }

    @Override
    public void update(Course course) {
        courseMapper.update(course);
    }

    @Override
    public Course findById(String courseId) {
        return courseMapper.findById(courseId);
    }

    @Override
    public List<Course> findAll() {
        return courseMapper.findAll();
    }

    @Override
    public PageResult<Course> page(CourseQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Course> list = courseMapper.list(param);
        Page<Course> page = (Page<Course>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
