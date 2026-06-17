package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.CourseSession;
import com.example.attendancesystem.entity.CourseSessionQueryParam;
import com.example.attendancesystem.service.CourseSessionService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/course-session")
public class CourseSessionController {
    @Autowired
    private CourseSessionService courseSessionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result insert(@RequestBody CourseSession session) {
        log.info("新增课次：{}", session);
        courseSessionService.insert(session);
        return Result.success("新增成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{sessionId}")
    public Result delete(@PathVariable Long sessionId) {
        log.info("删除课次：{}", sessionId);
        courseSessionService.delete(sessionId);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{sessionId}")
    public Result update(@PathVariable Long sessionId, @RequestBody CourseSession session) {
        log.info("修改课次：{}", sessionId);
        session.setSessionId(sessionId);
        courseSessionService.update(session);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{sessionId}")
    public Result findById(@PathVariable Long sessionId) {
        return Result.success(courseSessionService.findById(sessionId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(CourseSessionQueryParam param) {
        log.info("分页查询课次，筛选：{}", param);
        PageResult<CourseSession> pageResult = courseSessionService.page(param);
        return Result.success(pageResult);
    }
}
