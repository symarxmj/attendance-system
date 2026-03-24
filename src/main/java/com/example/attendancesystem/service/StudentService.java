package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Student;

public interface StudentService {
    String createStudent(Student student);
    Student getStudentById(String id);
}
