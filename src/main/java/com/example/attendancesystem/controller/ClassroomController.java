package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Classroom;
import com.example.attendancesystem.entity.ClassroomQueryParam;
import com.example.attendancesystem.service.ClassroomService;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/classroom")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result insert(@RequestBody Classroom classroom) {
        log.info("新增教室：{}", classroom);
        classroomService.insert(classroom);
        return Result.success("新增成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除教室ID：{}", id);
        classroomService.delete(id);
        return Result.success("删除成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody Classroom classroom) {
        log.info("修改教室ID：{}", id);
        classroom.setId(id);
        classroomService.update(classroom);
        return Result.success("更新成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(classroomService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(ClassroomQueryParam classroomQueryParam) {
        log.info("分页查询教室，筛选：{}", classroomQueryParam);
        PageResult<Classroom> pageResult = classroomService.page(classroomQueryParam);
        return Result.success(pageResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/all")
    public Result findAll() {
        ClassroomQueryParam param = new ClassroomQueryParam();
        param.setPage(1);
        param.setPageSize(1000);
        PageResult<Classroom> pageResult = classroomService.page(param);
        return Result.success(pageResult.getRows());
    }
}
