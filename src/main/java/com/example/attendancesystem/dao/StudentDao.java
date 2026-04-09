package com.example.attendancesystem.dao;

import com.example.attendancesystem.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentDao {

    @Insert("insert into student(student_id, student_name, gender, create_time) values(#{studentId}, #{studentName}, #{gender}, #{createTime})")
    void insertStudent(Student student);

    @Select("select student_id, student_name, gender, create_time from student where student_id = #{studentId}")
    Student findStudentById(@Param("studentId") String studentId);

    @Select("select * from student")
    List<Student> findAll();

    @Delete("delete from student where student_id = #{studentId}")
    void delete(String studentId);

    @Update("update student set student_name = #{studentName}, gender = #{gender} where student_id = #{studentId}")
    void updateStudent(Student student);
}
