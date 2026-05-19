package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result insertUser(@RequestBody User user){
        log.info("新增用户，用户信息为：{}", user);
        userService.insertUser(user);
        return Result.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable String userId){
        log.info("删除ID为：{}的用户信息", userId);
        userService.deleteUser(userId);
        return Result.success();
    }

}
