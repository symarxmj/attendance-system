package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSelectionQueryParam;
import com.example.attendancesystem.mapper.CourseSelectionMapper;
import com.example.attendancesystem.service.CourseSelectionService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseSelectionServiceImpl implements CourseSelectionService {

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Override
    public List<CourseSelection> findByStudentId(String studentId) {
        return courseSelectionMapper.findByStudentId(studentId);
    }

    @Override
    public void insert(CourseSelection cs) {
        cs.setSelectTime(LocalDateTime.now());
        courseSelectionMapper.insert(cs);
    }

    @Override
    public void delete(Long id) {
        courseSelectionMapper.delete(id);
    }

    @Override
    public void update(CourseSelection cs) {
        courseSelectionMapper.update(cs);
    }

    @Override
    public CourseSelection findById(Long id) {
        return courseSelectionMapper.findById(id);
    }

    @Override
    public PageResult<CourseSelection> page(CourseSelectionQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<CourseSelection> list = courseSelectionMapper.list(param);
        Page<CourseSelection> page = (Page<CourseSelection>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
