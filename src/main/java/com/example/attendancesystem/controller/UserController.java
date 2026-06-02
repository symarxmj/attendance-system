package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.entity.UserQueryParam;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result insertUser(@RequestBody User user) {
        log.info("新增用户，用户信息为：{}", user);
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("TEACHER");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        userService.insertUser(user);
        return Result.success("新增成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        log.info("删除ID为：{}的用户", id);
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("修改ID为：{}的用户信息", id);
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateUser(user);
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
}
