package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.Classroom;
import com.example.attendancesystem.entity.ClassroomQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassroomMapper {
    List<Classroom> list(ClassroomQueryParam classroomQueryParam);

    void insert(Classroom classroom);

    void delete(Integer id);

    void update(Classroom classroom);

    Classroom findById(Integer id);
}
