package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.CourseQueryParam;
import com.example.attendancesystem.service.CourseService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result insert(@RequestBody Course course) {
        log.info("新增课程：{}", course);
        courseService.insert(course);
        return Result.success("新增成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}")
    public Result delete(@PathVariable String courseId) {
        log.info("删除课程：{}", courseId);
        courseService.delete(courseId);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}")
    public Result update(@PathVariable String courseId, @RequestBody Course course) {
        log.info("修改课程：{}", courseId);
        course.setCourseId(courseId);
        courseService.update(course);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{courseId}")
    public Result findById(@PathVariable String courseId) {
        return Result.success(courseService.findById(courseId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(CourseQueryParam param) {
        log.info("分页查询课程，筛选：{}", param);
        PageResult<Course> pageResult = courseService.page(param);
        return Result.success(pageResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/all")
    public Result findAll() {
        return Result.success(courseService.findAll());
    }
}
