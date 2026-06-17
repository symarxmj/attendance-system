package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.*;
import com.example.attendancesystem.mapper.CourseMapper;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.service.CourseSessionService;
import com.example.attendancesystem.service.UserService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/today")
    public Result getTodaySessions(Principal principal) {
        String username = principal.getName();
        List<CourseSession> sessions = courseSessionService.getTodaySessionsForStudent(username);
        List<Long> sessionIds = new ArrayList<>();
        for (CourseSession s : sessions) {
            sessionIds.add(s.getSessionId());
        }

        // 对于超过20分钟的课次，自动将未签到学生置为缺勤
        LocalDateTime now = LocalDateTime.now();
        for (CourseSession s : sessions) {
            if (s.getSessionDate() != null) {
                long minutesDiff = ChronoUnit.MINUTES.between(s.getSessionDate(), now);
                if (minutesDiff > 20) {
                    attendanceService.autoMarkAbsent(s.getSessionId(), username);
                }
            }
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
            item.put("status", att != null ? att.getStatus() : null);

            // 判断课次是否已开始、是否超20分钟
            boolean notStarted = session.getSessionDate() != null
                && ChronoUnit.MINUTES.between(session.getSessionDate(), now) < 0;
            item.put("notStarted", notStarted);
            boolean canCheckIn = session.getSessionDate() != null;
            if (canCheckIn) {
                long diff = ChronoUnit.MINUTES.between(session.getSessionDate(), now);
                canCheckIn = diff >= 0 && diff <= 20;
            }
            item.put("canCheckIn", canCheckIn);

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

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return Result.error("状态不能为空");
        }
        log.info("修改考勤记录 {} 状态为：{}", id, status);
        attendanceService.updateStatus(id, status);
        return Result.success("更新成功");
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

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/check-in-force")
    public Result checkInForce(@RequestBody Map<String, Object> body, Principal principal) {
        Long sessionId = Long.valueOf(body.get("sessionId").toString());
        String studentId = body.get("studentId").toString();
        String status = body.get("status") != null ? body.get("status").toString() : "NORMAL";
        log.info("管理员/教师 {} 为学生 {} 补签，节次ID：{}，状态：{}", principal.getName(), studentId, sessionId, status);
        String message = attendanceService.checkInForce(sessionId, studentId, status);
        return Result.success(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/by-course")
    public Result getByCourse(@RequestParam String courseId) {
        log.info("按课程查询考勤：{}", courseId);
        List<Map<String, Object>> data = attendanceService.getAttendanceByCourse(courseId);
        return Result.success(data);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/export")
    public void export(@RequestParam String courseId, Principal principal,
                       HttpServletResponse response) throws IOException {
        // 教师只能导出自己教授的课程
        User currentUser = userService.findByUsername(principal.getName());
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('课程不存在');window.close();</script>");
            return;
        }

        // 检查权限：非 ADMIN 则验证是否为该课程教师
        if (!"ADMIN".equals(currentUser.getRole())) {
            if (course.getTeacherId() == null || !course.getTeacherId().equals(currentUser.getId())) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<script>alert('您无权导出该课程的考勤记录');window.close();</script>");
                return;
            }
        }

        List<Map<String, Object>> data = attendanceService.getAttendanceByCourse(courseId);

        // 设置 CSV 响应头
        String filename = "考勤记录_" + courseId + "_" + java.time.LocalDate.now() + ".csv";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename
                + "\"; filename*=UTF-8''" + encodedFilename);
        // 写入 BOM 以兼容 Excel 中文显示
        PrintWriter writer = response.getWriter();
        writer.print('﻿');
        writer.println("课程号,课程名称,学号,姓名,签到状态");

        String courseName = course.getCourseName() != null ? course.getCourseName() : courseId;
        for (Map<String, Object> row : data) {
            String statusCn = statusToChinese((String) row.get("status"));
            String studentId = (String) row.get("studentId");
            String studentName = (String) row.get("studentName");
            if (studentName == null) studentName = "";

            writer.println(csvEscape(courseId) + "," +
                    csvEscape(courseName) + "," +
                    csvEscape(studentId) + "," +
                    csvEscape(studentName) + "," +
                    csvEscape(statusCn));
        }
        writer.flush();
    }

    private String statusToChinese(String status) {
        if (status == null) return "缺勤";
        switch (status) {
            case "NORMAL": return "正常";
            case "LATE": return "迟到";
            case "EARLY": return "早退";
            case "ABSENT": return "缺勤";
            default: return status;
        }
    }

    private String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
