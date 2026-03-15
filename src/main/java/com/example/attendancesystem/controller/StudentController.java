package com.example.attendancesystem.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @GetMapping("/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("姓名", "Tom");
        info.put("专业", "计算机科学与技术");
        return info;
    }

    @PostMapping("/attendance")
    public String attendance(String id){
        return "学号：" + id + "的考勤成功！";
    }

    @GetMapping("/courses")
    public ArrayList<String> getCourses() {
        ArrayList<String> courses = new ArrayList<>();
        courses.add("数据结构");
        courses.add("操作系统");
        courses.add("计算机网络");
        courses.add("计算机组成原理");
        return courses;
    }

}
