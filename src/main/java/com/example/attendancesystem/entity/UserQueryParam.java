package com.example.attendancesystem.entity;

import lombok.Data;

@Data
public class UserQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String username;
    private String realName;
    private String role;
}
