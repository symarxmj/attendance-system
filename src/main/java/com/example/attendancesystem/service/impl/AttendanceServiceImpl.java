package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.AttendanceQueryParam;
import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.StatisticsDTO;
import com.example.attendancesystem.mapper.AttendanceMapper;
import com.example.attendancesystem.mapper.CourseSessionMapper;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CourseSessionMapper courseSessionMapper;

    private static final int CHECK_IN_WINDOW_MINUTES = 10;

    @Override
    public String checkIn(Long sessionId, String studentId, String ip) {
        Attendance existing = attendanceMapper.findBySessionIdAndStudentId(sessionId, studentId);
        if (existing != null) {
            return "已签到，无需重复签到";
        }

        // 根据签到时间与课次时间的差值判断状态
        String status = "NORMAL";
        CourseSession session = courseSessionMapper.findById(sessionId);
        if (session != null && session.getSessionDate() != null) {
            LocalDateTime sessionTime = session.getSessionDate();
            LocalDateTime now = LocalDateTime.now();
            long minutesDiff = ChronoUnit.MINUTES.between(sessionTime, now);
            if (minutesDiff > CHECK_IN_WINDOW_MINUTES) {
                status = "LATE";
            }
        }

        Attendance attendance = new Attendance();
        attendance.setSessionId(sessionId);
        attendance.setStudentId(studentId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(status);
        attendance.setIp(ip);
        attendance.setCreateTime(LocalDateTime.now());
        attendanceMapper.insert(attendance);
        return status.equals("LATE") ? "签到成功（迟到）" : "签到成功";
    }

    @Override
    public Map<Long, Attendance> getTodayAttendanceMap(String studentId, List<Long> sessionIds) {
        if (sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Attendance> list = attendanceMapper.findByStudentIdAndSessionIds(studentId, sessionIds);
        Map<Long, Attendance> map = new HashMap<>();
        for (Attendance a : list) {
            map.put(a.getSessionId(), a);
        }
        return map;
    }

    @Override
    public void delete(Long id) {
        attendanceMapper.delete(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        Attendance att = new Attendance();
        att.setId(id);
        att.setStatus(status);
        attendanceMapper.update(att);
    }

    @Override
    public Attendance findById(Long id) {
        return attendanceMapper.findById(id);
    }

    @Override
    public PageResult<Attendance> page(AttendanceQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Attendance> list = attendanceMapper.list(param);
        Page<Attendance> page = (Page<Attendance>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public StatisticsDTO getStudentStatistics(String studentId) {
        long total = attendanceMapper.countByStudentId(studentId);
        long normal = attendanceMapper.countByStudentIdAndStatus(studentId, "NORMAL");
        long late = attendanceMapper.countByStudentIdAndStatus(studentId, "LATE");
        long early = attendanceMapper.countByStudentIdAndStatus(studentId, "EARLY");
        long attended = normal + late + early;
        long absent = total - attended;
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalCount(total);
        dto.setPresentCount(normal + late + early);
        dto.setAbsentCount(absent);
        dto.setAttendanceRate(total > 0 ? Math.round(attended * 10000.0 / total) / 100.0 : 0);
        dto.setNormalCount(normal);
        dto.setLateCount(late);
        return dto;
    }

    @Override
    public void deleteByStudentId(String studentId) {
        attendanceMapper.deleteByStudentId(studentId);
    }
}
