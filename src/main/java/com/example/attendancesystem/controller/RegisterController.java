package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Transactional
    public Result register(@RequestBody User user) {
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在！");
        }

        // 1. 创建登录账号
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("STUDENT");
        user.setCreateTime(LocalDateTime.now());
        userService.insertUser(user);

        // 2. 同步创建学生记录，使 student 表与 user 表保持一致
        Student student = new Student();
        student.setStudentId(user.getUsername());
        student.setStudentName(
            user.getRealName() != null && !user.getRealName().isEmpty()
                ? user.getRealName()
                : user.getUsername()
        );
        student.setCreateTime(LocalDateTime.now());
        studentService.insertStudent(student);

        return Result.success("注册成功！");
    }
}
