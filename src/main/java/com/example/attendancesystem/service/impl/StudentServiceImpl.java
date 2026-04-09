package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.dao.StudentDao;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.service.StudentService;
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
    public Student findStudentById(String studentId) {
        return studentDao.findStudentById(studentId);
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
}
