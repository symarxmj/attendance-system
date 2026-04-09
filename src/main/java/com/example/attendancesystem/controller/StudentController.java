package com.example.attendancesystem.controller;

import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.Result;
import com.example.attendancesystem.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 新增学生
    @PostMapping("/insert")
    public Result insertStudent(@RequestBody Student student){
        System.out.println("新增学生信息：" + student);
        String message = studentService.insertStudent(student);
        return Result.success(message);
    }

    // 根据学号删除学生
    @DeleteMapping("/{studentId}")
    public Result deleteStudent(@PathVariable String studentId){
        System.out.println("根据学号：" + studentId + "，删除学生信息");
        String message = studentService.deleteStudent(studentId);
        return Result.success(message);
    }

    // 根据学号查询学生
    @GetMapping("/{studentId}")
    public Result findStudentById(@PathVariable String studentId) {
        System.out.println("根据学号：" + studentId + "，查询学生信息");
        Student student = studentService.findStudentById(studentId);
        return Result.success(student);
    }

    // 查询所有学生
    @GetMapping("/findAll")
    public Result findAll(){
        List<Student> studentList = studentService.findAll();
        return Result.success(studentList);
    }

    // 根据学号修改学生信息
    @PutMapping("/{studentId}")
    public Result updateStudent(@PathVariable String studentId, @RequestBody Student student){
        System.out.println("根据学号：" + studentId + "，修改学生信息");
        student.setStudentId(studentId);
        String message = studentService.updateStudent(student);
        return Result.success(message);
    }
}