package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 1. 检查用户名是否已存在
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在！");
        }

        // 2. 密码加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 设置默认角色和创建时间
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        user.setCreateTime(LocalDateTime.now());

        // 4. 保存用户信息
        userService.insertUser(user);

        // 5. 返回注册成功响应
        return Result.success("注册成功！");
    }
}
