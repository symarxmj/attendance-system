package com.example.attendancesystem.dao;

import com.example.attendancesystem.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class StudentDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void insert(Student student) {
        String sql = "INSERT INTO students (student_id, name, class_name, age) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, student.getStudentId(), student.getName(), student.getClassName(), student.getAge());
    }

    public Student findById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Student>() {
            @Override
            public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setName(rs.getString("name"));
                student.setClassName(rs.getString("class_name"));
                student.setAge(rs.getInt("age"));
                return student;
            }
        }, studentId);
    }
}
