package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.mapper.AttendanceMapper;
import com.example.attendancesystem.mapper.CourseSelectionMapper;
import com.example.attendancesystem.mapper.StudentMapper;
import com.example.attendancesystem.mapper.UserMapper;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.entity.StudentQueryParam;
import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.util.ImportResult;
import com.example.attendancesystem.util.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Override
    public String insertStudent(Student student) {
        if(student.getStudentName() == null || student.getStudentName().isEmpty()){
            throw new RuntimeException("姓名不能为空！");
        }
        student.setCreateTime(LocalDateTime.now());
        studentMapper.insertStudent(student);
        return "创建成功";
    }

    @Override
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    @Override
    public String deleteStudent(String studentId) {
        // 级联删除关联数据
        attendanceMapper.deleteByStudentId(studentId);
        courseSelectionMapper.deleteByStudentId(studentId);
        userMapper.deleteByUsername(studentId);
        studentMapper.delete(studentId);
        return "删除成功";
    }

    @Override
    public String updateStudent(Student student) {
        studentMapper.updateStudent(student);
        return "信息更新成功";
    }

    @Override
    public Student findById(String studentId) {
        return studentMapper.getById(studentId);
    }

    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());
        List<Student> studentList = studentMapper.list(studentQueryParam);
        Page<Student> page = (Page<Student>) studentList;
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public ImportResult importFromExcel(String filePath) {
        ImportResult result = new ImportResult();
        List<Student> studentBatch = new ArrayList<>();
        List<User> userBatch = new ArrayList<>();
        int batchSize = 200;
        String defaultPassword = passwordEncoder.encode("swufe");

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String studentId = getCellValue(row.getCell(0));
                    String studentName = getCellValue(row.getCell(1));
                    String gender = getCellValue(row.getCell(2));

                    if (studentId.isEmpty()) {
                        result.incrementFail("第" + (i + 1) + "行：学号为空");
                        continue;
                    }
                    if (studentName.isEmpty()) {
                        result.incrementFail("第" + (i + 1) + "行：姓名为空");
                        continue;
                    }

                    Student existing = studentMapper.getById(studentId);
                    if (existing != null) {
                        result.incrementFail("第" + (i + 1) + "行：学号 " + studentId + " 已存在");
                        continue;
                    }

                    Student student = new Student();
                    student.setStudentId(studentId);
                    student.setStudentName(studentName);
                    student.setGender(gender);
                    student.setCreateTime(LocalDateTime.now());
                    studentBatch.add(student);

                    // 同步创建 user 记录（默认密码 swufe）
                    User existingUser = userMapper.findByUsername(studentId);
                    if (existingUser == null) {
                        User user = new User();
                        user.setUsername(studentId);
                        user.setPassword(defaultPassword);
                        user.setRealName(studentName);
                        user.setRole("STUDENT");
                        user.setCreateTime(LocalDateTime.now());
                        userBatch.add(user);
                    }

                    result.incrementSuccess();

                    if (studentBatch.size() >= batchSize) {
                        studentMapper.insertBatch(studentBatch);
                        studentBatch.clear();
                        if (!userBatch.isEmpty()) {
                            userMapper.insertBatch(userBatch);
                            userBatch.clear();
                        }
                    }
                } catch (Exception e) {
                    result.incrementFail("第" + (i + 1) + "行：解析失败 - " + e.getMessage());
                }
            }

            if (!studentBatch.isEmpty()) {
                studentMapper.insertBatch(studentBatch);
            }
            if (!userBatch.isEmpty()) {
                userMapper.insertBatch(userBatch);
            }
        } catch (Exception e) {
            throw new RuntimeException("读取Excel文件失败：" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public byte[] generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");
            Row header = sheet.createRow(0);
            String[] titles = {"学号", "姓名", "性别"};
            for (int i = 0; i < titles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(titles[i]);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成模板失败：" + e.getMessage(), e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
