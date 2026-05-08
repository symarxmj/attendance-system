package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentMapper {
    void insertStudent(Student student);

    @Select("select * from student")
    List<Student> findAll();

    void delete(String studentId);

    void updateStudent(Student student);

    List<Student> list(StudentQueryParam studentQueryParam);
}
