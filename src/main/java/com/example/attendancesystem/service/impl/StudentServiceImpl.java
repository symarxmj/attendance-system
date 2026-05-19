package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.mapper.StudentMapper;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.StudentQueryParam;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public String insertStudent(Student student) {
        if(student.getStudentName() == null || student.getStudentName().isEmpty()){
            throw new RuntimeException("姓名不能为空！");
        }
        student.setCreateTime(LocalDateTime.now());
        studentMapper.insertStudent(student);
        return "创建成功";
    }

    @Override
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    @Override
    public String deleteStudent(String studentId) {
        studentMapper.delete(studentId);
        return "删除成功";
    }

    @Override
    public String updateStudent(Student student) {
        studentMapper.updateStudent(student);
        return "信息更新成功";
    }

    @Override
    public Student findById(String studentId) {
        return studentMapper.getById(studentId);
    }

    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        // 设置分页参数
        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());

        // 分页查询当前页数据
        List<Student> studentList = studentMapper.list(studentQueryParam);

        // 封装为 PageResult 对象
        Page<Student> page = (Page<Student>) studentList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
