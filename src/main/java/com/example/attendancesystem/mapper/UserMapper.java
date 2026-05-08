package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    void insertUser(User user);

    void delete(String userId);

    User findByUsername(String username);
}
