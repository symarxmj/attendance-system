package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.dao.StudentDao;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.StudentQueryParam;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Override
    public String insertStudent(Student student) {
        if(student.getStudentName() == null || student.getStudentName().isEmpty()){
            throw new RuntimeException("姓名不能为空！");
        }
        studentDao.insertStudent(student);
        return "创建成功";
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public String deleteStudent(String studentId) {
        studentDao.delete(studentId);
        return "删除成功";
    }

    @Override
    public String updateStudent(Student student) {
        studentDao.updateStudent(student);
        return "信息更新成功";
    }

    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        // 设置分页参数
        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());

        // 分页查询当前页数据
        List<Student> studentList = studentDao.list(studentQueryParam);

        // 封装为 PageResult 对象
        Page<Student> page = (Page<Student>) studentList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
