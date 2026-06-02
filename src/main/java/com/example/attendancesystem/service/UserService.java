package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.entity.UserQueryParam;
import com.example.attendancesystem.util.PageResult;

public interface UserService {
    void insertUser(User user);

    void deleteUser(Long id);

    User findByUsername(String username);

    User findById(Long id);

    void updateUser(User user);

    PageResult<User> page(UserQueryParam param);
}
