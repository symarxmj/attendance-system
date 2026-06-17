package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.AttendanceQueryParam;
import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.StatisticsDTO;
import com.example.attendancesystem.mapper.AttendanceMapper;
import com.example.attendancesystem.mapper.CourseMapper;
import com.example.attendancesystem.mapper.CourseSelectionMapper;
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
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CourseSessionMapper courseSessionMapper;

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Autowired
    private CourseMapper courseMapper;

    private static final int CHECK_IN_WINDOW_MINUTES = 10;
    private static final int CHECK_IN_LATE_MINUTES = 20;

    @Override
    public String checkInForce(Long sessionId, String studentId, String status) {
        Attendance existing = attendanceMapper.findBySessionIdAndStudentId(sessionId, studentId);
        if (existing != null) {
            // 已有记录，更新状态
            existing.setStatus(status);
            attendanceMapper.update(existing);
            return "状态已更新为" + statusLabel(status);
        }
        Attendance attendance = new Attendance();
        attendance.setSessionId(sessionId);
        attendance.setStudentId(studentId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(status);
        attendance.setCreateTime(LocalDateTime.now());
        attendanceMapper.insert(attendance);
        return "补签成功（" + statusLabel(status) + "）";
    }

    private String statusLabel(String status) {
        switch (status) {
            case "NORMAL": return "正常";
            case "LATE": return "迟到";
            case "EARLY": return "早退";
            case "ABSENT": return "缺勤";
            default: return status;
        }
    }

    @Override
    public String checkIn(Long sessionId, String studentId, String ip) {
        Attendance existing = attendanceMapper.findBySessionIdAndStudentId(sessionId, studentId);
        if (existing != null) {
            return "已签到，无需重复签到";
        }

        CourseSession session = courseSessionMapper.findById(sessionId);
        if (session == null || session.getSessionDate() == null) {
            return "课次信息不存在，无法签到";
        }

        LocalDateTime sessionTime = session.getSessionDate();
        LocalDateTime now = LocalDateTime.now();
        long minutesDiff = ChronoUnit.MINUTES.between(sessionTime, now);

        // 签到未开始
        if (minutesDiff < 0) {
            return "签到未开始，请在课次开始后签到";
        }

        // 超过20分钟，不可签到
        if (minutesDiff > CHECK_IN_LATE_MINUTES) {
            return "签到已结束（课次开始20分钟后不可签到），请联系教师手动修改考勤状态";
        }

        // 根据签到时间与课次时间的差值判断状态
        String status = minutesDiff <= CHECK_IN_WINDOW_MINUTES ? "NORMAL" : "LATE";

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

    @Override
    public void autoMarkAbsent(Long sessionId, String studentId) {
        Attendance existing = attendanceMapper.findBySessionIdAndStudentId(sessionId, studentId);
        if (existing == null) {
            Attendance att = new Attendance();
            att.setSessionId(sessionId);
            att.setStudentId(studentId);
            att.setStatus("ABSENT");
            att.setCreateTime(LocalDateTime.now());
            attendanceMapper.insert(att);
        }
    }

    @Override
    public void autoMarkAbsentForSession(Long sessionId) {
        CourseSession session = courseSessionMapper.findById(sessionId);
        if (session == null || session.getCourseId() == null) {
            return;
        }
        // 获取该课程所有选课学生
        List<CourseSelection> selections = courseSelectionMapper.findByCourseId(session.getCourseId());
        for (CourseSelection cs : selections) {
            autoMarkAbsent(sessionId, cs.getStudentId());
        }
    }

    @Override
    public List<Map<String, Object>> getAttendanceByCourse(String courseId) {
        // 1. 获取课程所有课次
        List<CourseSession> sessions = courseSessionMapper.findByCourseId(courseId);
        List<Long> sessionIds = sessions.stream().map(CourseSession::getSessionId).collect(Collectors.toList());

        // 2. 对超过20分钟的课次，自动将未签到学生置为缺勤
        LocalDateTime now = LocalDateTime.now();
        for (CourseSession s : sessions) {
            if (s.getSessionDate() != null) {
                long diff = ChronoUnit.MINUTES.between(s.getSessionDate(), now);
                if (diff > 20) {
                    autoMarkAbsentForSession(s.getSessionId());
                }
            }
        }

        // 3. 获取已有考勤记录
        List<Attendance> records = sessionIds.isEmpty() ? List.of()
                : attendanceMapper.findBySessionIds(sessionIds);

        // 4. 获取课程信息（用于课程名）
        Course course = courseMapper.findById(courseId);
        String courseName = course != null ? course.getCourseName() : courseId;

        // 5. 获取所有选课学生
        List<CourseSelection> selections = courseSelectionMapper.findByCourseId(courseId);

        // 6. 记录已有考勤的学生（按 studentId 去重）
        Set<String> studentsWithRecords = records.stream()
                .map(Attendance::getStudentId)
                .collect(Collectors.toSet());

        // 7. 构建结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Attendance a : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", a.getId());
            item.put("sessionId", a.getSessionId());
            item.put("studentId", a.getStudentId());
            item.put("studentName", a.getStudentName());
            item.put("courseName", a.getCourseName() != null ? a.getCourseName() : courseName);
            item.put("sessionDate", a.getSessionDate());
            item.put("checkInTime", a.getCheckInTime());
            item.put("status", a.getStatus());
            result.add(item);
        }

        // 8. 补充从未签到的学生（每个课次一条 ABSENT 记录）
        for (CourseSelection cs : selections) {
            if (!studentsWithRecords.contains(cs.getStudentId())) {
                for (CourseSession s : sessions) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", null);
                    item.put("sessionId", s.getSessionId());
                    item.put("studentId", cs.getStudentId());
                    item.put("studentName", cs.getStudentName());
                    item.put("courseName", courseName);
                    item.put("sessionDate", s.getSessionDate());
                    item.put("checkInTime", null);
                    item.put("status", "ABSENT");
                    result.add(item);
                }
            }
        }

        return result;
    }
}
