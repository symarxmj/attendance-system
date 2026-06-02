package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.*;
import com.example.attendancesystem.mapper.CourseMapper;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.service.CourseSessionService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseSessionService courseSessionService;

    @Autowired
    private CourseMapper courseMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/today")
    public Result getTodaySessions(Principal principal) {
        String username = principal.getName();
        List<CourseSession> sessions = courseSessionService.getTodaySessionsForStudent(username);
        List<Long> sessionIds = new ArrayList<>();
        for (CourseSession s : sessions) {
            sessionIds.add(s.getSessionId());
        }
        Map<Long, Attendance> attendanceMap = attendanceService.getTodayAttendanceMap(username, sessionIds);

        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSession session : sessions) {
            Map<String, Object> item = new HashMap<>();
            item.put("sessionId", session.getSessionId());
            item.put("courseId", session.getCourseId());
            Course course = courseMapper.findById(session.getCourseId());
            item.put("courseName", course != null ? course.getCourseName() : session.getCourseId());
            item.put("sessionDate", session.getSessionDate());
            Attendance att = attendanceMap.get(session.getSessionId());
            item.put("checkedIn", att != null);
            item.put("checkInTime", att != null ? att.getCheckInTime() : null);
            result.add(item);
        }
        return Result.success(result);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PostMapping("/check-in")
    public Result checkIn(@RequestBody Map<String, Object> body, Principal principal,
                          HttpServletRequest request) {
        Long sessionId = Long.valueOf(body.get("sessionId").toString());
        String username = principal.getName();
        String ip = request.getRemoteAddr();
        log.info("学生 {} 签到，节次ID：{}，IP：{}", username, sessionId, ip);
        String message = attendanceService.checkIn(sessionId, username, ip);
        return Result.success(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除考勤记录：{}", id);
        attendanceService.delete(id);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(attendanceService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(AttendanceQueryParam param) {
        log.info("分页查询考勤，筛选：{}", param);
        PageResult<Attendance> pageResult = attendanceService.page(param);
        return Result.success(pageResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/statistics")
    public Result statistics(@RequestParam String studentId) {
        StatisticsDTO dto = attendanceService.getStudentStatistics(studentId);
        return Result.success(dto);
    }
}
