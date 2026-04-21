package com.example.attendancesystem.dao;

import com.example.attendancesystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    void insertUser(User user);

    void delete(String userId);
}
