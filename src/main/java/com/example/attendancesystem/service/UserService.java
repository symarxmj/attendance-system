package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.User;

public interface UserService {
    void insertUser(User user);

    void deleteUser(String userId);
}
