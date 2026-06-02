package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.CourseSessionQueryParam;
import com.example.attendancesystem.mapper.CourseSelectionMapper;
import com.example.attendancesystem.mapper.CourseSessionMapper;
import com.example.attendancesystem.service.CourseSessionService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSessionServiceImpl implements CourseSessionService {

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Autowired
    private CourseSessionMapper courseSessionMapper;

    @Override
    public List<CourseSession> getTodaySessionsForStudent(String studentId) {
        List<CourseSelection> selections = courseSelectionMapper.findByStudentId(studentId);
        if (selections.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> courseIds = selections.stream()
                .map(CourseSelection::getCourseId)
                .collect(Collectors.toList());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return courseSessionMapper.findByCourseIdsAndDate(courseIds, today);
    }

    @Override
    public void insert(CourseSession session) {
        courseSessionMapper.insert(session);
    }

    @Override
    public void delete(Long sessionId) {
        courseSessionMapper.delete(sessionId);
    }

    @Override
    public void update(CourseSession session) {
        courseSessionMapper.update(session);
    }

    @Override
    public CourseSession findById(Long sessionId) {
        return courseSessionMapper.findById(sessionId);
    }

    @Override
    public PageResult<CourseSession> page(CourseSessionQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<CourseSession> list = courseSessionMapper.list(param);
        Page<CourseSession> page = (Page<CourseSession>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
