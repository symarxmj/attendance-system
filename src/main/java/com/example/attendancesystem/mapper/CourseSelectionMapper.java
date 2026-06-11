package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSelectionQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseSelectionMapper {
    List<CourseSelection> findByStudentId(String studentId);
    void insert(CourseSelection cs);
    void delete(Long id);
    void update(CourseSelection cs);
    CourseSelection findById(Long id);
    List<CourseSelection> list(CourseSelectionQueryParam param);

    void deleteByStudentId(String studentId);
}
