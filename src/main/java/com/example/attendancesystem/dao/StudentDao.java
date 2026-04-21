package com.example.attendancesystem.dao;

import com.example.attendancesystem.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentDao {

    void insertStudent(Student student);

    Student findStudentById(@Param("studentId") String studentId);

    @Select("select * from student")
    List<Student> findAll();

    void delete(String studentId);

    void updateStudent(Student student);
}
