package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.entity.UserQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(User user);

    void delete(Long id);

    User findByUsername(String username);

    User findById(Long id);

    void updateUser(User user);

    List<User> list(UserQueryParam param);

    int insertBatch(List<User> list);

    void deleteByUsername(String username);
}
