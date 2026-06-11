package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.entity.UserQueryParam;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Transactional
    public Result insertUser(@RequestBody User user) {
        log.info("新增用户，用户信息为：{}", user);
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("TEACHER");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        userService.insertUser(user);
        // 如果是学生角色，同步创建 student 记录
        if ("STUDENT".equals(user.getRole())) {
            createStudentIfNotExists(user.getUsername(), user.getRealName());
        }
        return Result.success("新增成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Transactional
    public Result deleteUser(@PathVariable Long id) {
        log.info("删除ID为：{}的用户", id);
        User user = userService.findById(id);
        if (user != null) {
            // 如果是学生角色，同步删除 student 记录
            if ("STUDENT".equals(user.getRole())) {
                studentService.deleteStudent(user.getUsername());
            }
            userService.deleteUser(id);
        }
        return Result.success("删除成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("修改ID为：{}的用户信息", id);
        User oldUser = userService.findById(id);
        String oldRole = oldUser != null ? oldUser.getRole() : null;

        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateUser(user);

        // 角色改为 STUDENT → 创建 student 记录
        if ("STUDENT".equals(user.getRole()) && !"STUDENT".equals(oldRole)) {
            createStudentIfNotExists(oldUser.getUsername(), oldUser.getRealName());
        }
        // 角色从 STUDENT 改为其他 → 删除 student 记录
        if (!"STUDENT".equals(user.getRole()) && "STUDENT".equals(oldRole)) {
            studentService.deleteStudent(oldUser.getUsername());
        }
        // realName 同步到 student 表
        if (user.getRealName() != null && !user.getRealName().isEmpty()) {
            User updated = userService.findById(id);
            if (updated != null && "STUDENT".equals(updated.getRole())) {
                Student student = studentService.findById(updated.getUsername());
                if (student != null) {
                    student.setStudentName(user.getRealName());
                    studentService.updateStudent(student);
                }
            }
        }
        return Result.success("更新成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        User user = userService.findById(id);
        user.setPassword(null);
        return Result.success(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public Result page(UserQueryParam param) {
        log.info("分页查询用户，筛选条件：{}", param);
        PageResult<User> pageResult = userService.page(param);
        for (User user : pageResult.getRows()) {
            user.setPassword(null);
        }
        return Result.success(pageResult);
    }

    // 为新增的学生用户同步创建 student 记录
    private void createStudentIfNotExists(String username, String realName) {
        Student existing = studentService.findById(username);
        if (existing != null) return;
        Student student = new Student();
        student.setStudentId(username);
        student.setStudentName(realName != null && !realName.isEmpty() ? realName : username);
        student.setCreateTime(LocalDateTime.now());
        studentService.insertStudent(student);
    }
}