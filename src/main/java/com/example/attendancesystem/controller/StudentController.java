package com.example.attendancesystem.controller;

import com.example.attendancesystem.model.Result;
import com.example.attendancesystem.model.Student;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

//    @GetMapping("/{id}")
//    public String getStudentById(@PathVariable String id){
//        return "学号为：" + id + "的学生信息";
//    }

    @GetMapping("/search")
    public String searchStudent(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page
    ){
        return "搜索学生姓名：" + name + "，页码：" + page;
    }

    @PostMapping("/create")
    public String createStudent(@RequestBody Student student){
        return "创建学生：" + student.getName() + "，学号：" + student.getStudentId();
    }

    @GetMapping("/info/{id}")
    public Result<Student> getStudentById(@PathVariable String id){
        Student student = new Student();
        student.setStudentId(id);
        student.setName("Tom");
        return Result.success(student);
    }

    @PostMapping("/update")
    public Result<String> updateStudent(@RequestBody Student student){
        // 更新学生信息的相关逻辑代码
        return Result.success("更新成功");
    }

    @GetMapping("/list")
    public Result<List<Student>> getStudentList(
            @RequestParam String className,
            @RequestParam(defaultValue = "1") int page
    ){
        // 查询符合条件的学生列表
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student("20210001", "Tom", "计算机科学与技术", 18));
        return Result.success(studentList);
    }

    @PostMapping("/attendance/updata")
    public Result<String> updateAttendance(@RequestBody Map<String, Object> params){
        // 更新考勤信息
        return Result.success("更新成功");
    }

}
