package com.example.attendancesystem.controller;

import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.Result;
import com.example.attendancesystem.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody Student student){
        return Result.success(studentService.createStudent(student));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable String id){
        return Result.success(studentService.getStudentById(id));
    }
}