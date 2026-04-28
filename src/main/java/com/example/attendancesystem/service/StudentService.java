package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.StudentQueryParam;
import com.example.attendancesystem.util.PageResult;

import java.util.List;

public interface StudentService {
    String insertStudent(Student student);

    List<Student> findAll();

    String deleteStudent(String studentId);

    String updateStudent(Student student);

    PageResult<Student> page(StudentQueryParam studentQueryParam);
}
