package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.CourseSelection;
import com.example.attendancesystem.entity.CourseSelectionQueryParam;
import com.example.attendancesystem.service.CourseSelectionService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/course-selection")
public class CourseSelectionController {
    @Autowired
    private CourseSelectionService courseSelectionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public Result insert(@RequestBody CourseSelection cs) {
        log.info("新增选课：{}", cs);
        courseSelectionService.insert(cs);
        return Result.success("新增成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除选课：{}", id);
        courseSelectionService.delete(id);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody CourseSelection cs) {
        log.info("修改选课：{}", id);
        cs.setId(id);
        courseSelectionService.update(cs);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(courseSelectionService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(CourseSelectionQueryParam param) {
        log.info("分页查询选课，筛选：{}", param);
        PageResult<CourseSelection> pageResult = courseSelectionService.page(param);
        return Result.success(pageResult);
    }
}
