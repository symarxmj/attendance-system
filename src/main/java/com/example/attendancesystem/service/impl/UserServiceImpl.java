package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.dao.UserDao;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void insertUser(User user) {
        if(user.getUsername() == null || user.getUsername().isEmpty()){
            throw new RuntimeException("用户名不能为空！");
        }
        userDao.insertUser(user);
    }

    @Override
    public void deleteUser(String userId) {
        userDao.delete(userId);
    }
}
