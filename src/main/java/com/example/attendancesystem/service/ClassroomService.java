package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Classroom;
import com.example.attendancesystem.entity.ClassroomQueryParam;
import com.example.attendancesystem.util.PageResult;

public interface ClassroomService {
    PageResult<Classroom> page(ClassroomQueryParam classroomQueryParam);
    void insert(Classroom classroom);
    void delete(Integer id);
    void update(Classroom classroom);
    Classroom findById(Integer id);
}
