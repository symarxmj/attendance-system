package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Classroom;
import com.example.attendancesystem.entity.ClassroomQueryParam;
import com.example.attendancesystem.mapper.ClassroomMapper;
import com.example.attendancesystem.service.ClassroomService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public PageResult<Classroom> page(ClassroomQueryParam classroomQueryParam) {
        PageHelper.startPage(classroomQueryParam.getPage(), classroomQueryParam.getPageSize());
        List<Classroom> classroomList = classroomMapper.list(classroomQueryParam);
        Page<Classroom> page = (Page<Classroom>) classroomList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void insert(Classroom classroom) {
        classroom.setCreateTime(LocalDateTime.now());
        classroomMapper.insert(classroom);
    }

    @Override
    public void delete(Integer id) {
        classroomMapper.delete(id);
    }

    @Override
    public void update(Classroom classroom) {
        classroomMapper.update(classroom);
    }

    @Override
    public Classroom findById(Integer id) {
        return classroomMapper.findById(id);
    }
}
