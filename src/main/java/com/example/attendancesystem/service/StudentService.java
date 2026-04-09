package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Student;

import java.util.List;

public interface StudentService {
    String insertStudent(Student student);

    Student findStudentById(String id);

    List<Student> findAll();

    String deleteStudent(String studentId);

    String updateStudent(Student student);
}
