package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.AttendanceQueryParam;
import com.example.attendancesystem.entity.StatisticsDTO;
import com.example.attendancesystem.mapper.AttendanceMapper;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public String checkIn(Long sessionId, String studentId, String ip) {
        Attendance existing = attendanceMapper.findBySessionIdAndStudentId(sessionId, studentId);
        if (existing != null) {
            return "已签到，无需重复签到";
        }
        Attendance attendance = new Attendance();
        attendance.setSessionId(sessionId);
        attendance.setStudentId(studentId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("PRESENT");
        attendance.setIp(ip);
        attendance.setCreateTime(LocalDateTime.now());
        attendanceMapper.insert(attendance);
        return "签到成功";
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
        long present = attendanceMapper.countByStudentIdAndStatus(studentId, "PRESENT");
        long absent = total - present;
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalCount(total);
        dto.setPresentCount(present);
        dto.setAbsentCount(absent);
        dto.setAttendanceRate(total > 0 ? Math.round(present * 10000.0 / total) / 100.0 : 0);
        return dto;
    }
}
