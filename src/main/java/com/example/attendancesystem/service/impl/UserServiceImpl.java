package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.entity.UserQueryParam;
import com.example.attendancesystem.mapper.UserMapper;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void insertUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空！");
        }
        userMapper.insertUser(user);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.delete(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public PageResult<User> page(UserQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<User> userList = userMapper.list(param);
        Page<User> page = (Page<User>) userList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteByUsername(String username) {
        userMapper.deleteByUsername(username);
    }

    @Override
    public int insertBatch(List<User> list) {
        return userMapper.insertBatch(list);
    }
}
