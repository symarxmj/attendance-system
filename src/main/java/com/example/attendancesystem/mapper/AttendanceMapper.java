package com.example.attendancesystem.mapper;

import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.AttendanceQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceMapper {
    void insert(Attendance attendance);
    Attendance findBySessionIdAndStudentId(@Param("sessionId") Long sessionId,
                                           @Param("studentId") String studentId);
    List<Attendance> findByStudentIdAndSessionIds(@Param("studentId") String studentId,
                                                  @Param("sessionIds") List<Long> sessionIds);
    void delete(Long id);
    void update(Attendance attendance);
    Attendance findById(Long id);
    List<Attendance> list(AttendanceQueryParam param);

    long countByStudentId(@Param("studentId") String studentId);

    long countByStudentIdAndStatus(@Param("studentId") String studentId, @Param("status") String status);

    void deleteByStudentId(@Param("studentId") String studentId);
}
