package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.CourseSessionQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseSessionMapper {
    List<CourseSession> findByCourseIdsAndDate(@Param("courseIds") List<String> courseIds,
                                               @Param("sessionDate") String sessionDate);
    void insert(CourseSession session);
    void delete(Long sessionId);
    void update(CourseSession session);
    CourseSession findById(Long sessionId);
    List<CourseSession> list(CourseSessionQueryParam param);

    List<CourseSession> findByCourseId(@Param("courseId") String courseId);
}
