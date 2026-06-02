package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.StudentQueryParam;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.ImportResult;
import com.example.attendancesystem.util.PageResult;
import com.example.attendancesystem.util.Result;
import com.example.attendancesystem.entity.Student;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 新增学生
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public Result insertStudent(@RequestBody Student student){
        log.info("新增学生信息：, {}",  student);
        String message = studentService.insertStudent(student);
        return Result.success(message);
    }

    // 根据学号删除学生
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{studentId}")
    public Result deleteStudent(@PathVariable String studentId){
        log.info("删除学号为：{}的学生信息", studentId);
        String message = studentService.deleteStudent(studentId);
        return Result.success(message);
    }

    // 查询所有学生
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping
    public Result findAll(){
        List<Student> studentList = studentService.findAll();
        return Result.success(studentList);
    }

    // 根据学号查询学生信息
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{studentId}")
    public Result findById(@PathVariable String studentId){
        log.info("查询学号为：{}的学生信息", studentId);
        Student student = studentService.findById(studentId);
        return Result.success(student);
    }

    // 根据学号修改学生信息
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{studentId}")
    public Result updateStudent(@PathVariable String studentId, @RequestBody Student student){
        log.info("修改学号为：{}的学生信息", studentId);
        student.setStudentId(studentId);
        String message = studentService.updateStudent(student);
        return Result.success(message);
    }

    // 分页查询学生信息（支持筛选）
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result page(StudentQueryParam  studentQueryParam){
        log.info("根据筛选信息分页查询学生信息，筛选信息为：{}", studentQueryParam);
        PageResult<Student> pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }

    // 批量导入学生
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/import")
    public Result importStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error("文件格式不正确，仅支持 .xlsx 和 .xls 格式");
        }
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();
            String savedName = UUID.randomUUID() + "_" + filename;
            File dest = new File(uploadPath + savedName);
            file.transferTo(dest);
            ImportResult result = studentService.importFromExcel(dest.getAbsolutePath());
            dest.delete();
            return Result.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    // 下载导入模板
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            byte[] data = studentService.generateTemplate();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode("学生导入模板.xlsx", StandardCharsets.UTF_8));
            response.setContentLength(data.length);
            try (OutputStream os = response.getOutputStream()) {
                os.write(data);
            }
        } catch (IOException e) {
            log.error("模板下载失败", e);
        }
    }
}