package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.*;
import com.example.attendancesystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class PageController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSessionService courseSessionService;

    @Autowired
    private CourseSelectionService courseSelectionService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/welcome")
    public String welcome(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "welcome");
        model.addAttribute("role", extractRole(authentication));
        return "welcome";
    }

    @GetMapping("/student/list-page")
    public String studentList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "student");
        return "student-list";
    }

    @GetMapping("/student/add-page")
    public String addPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("student", new Student());
        model.addAttribute("currentPage", "student");
        return "student-form";
    }

    @GetMapping("/student/edit-page/{studentId}")
    public String editPage(@PathVariable String studentId, Model model, Principal principal) {
        Student student = studentService.findById(studentId);
        model.addAttribute("username", principal.getName());
        model.addAttribute("student", student);
        model.addAttribute("currentPage", "student");
        return "student-form";
    }

    @GetMapping("/student/import-page")
    public String studentImport(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "student");
        return "student-import";
    }

    @GetMapping("/user/list-page")
    public String userList(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "user");
        model.addAttribute("role", extractRole(authentication));
        return "user-list";
    }

    @GetMapping("/user/add-page")
    public String userAddPage(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "user");
        model.addAttribute("role", extractRole(authentication));
        model.addAttribute("user", new User());
        return "user-form";
    }

    @GetMapping("/user/edit-page/{id}")
    public String userEditPage(@PathVariable Long id, Model model, Principal principal,
                               Authentication authentication) {
        User user = userService.findById(id);
        user.setPassword(null);
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "user");
        model.addAttribute("role", extractRole(authentication));
        model.addAttribute("user", user);
        return "user-form";
    }

    // ========== 教室管理页面 ==========
    @GetMapping("/classroom/list-page")
    public String classroomList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "classroom");
        return "classroom-list";
    }

    @GetMapping("/classroom/add-page")
    public String classroomAddPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "classroom");
        model.addAttribute("classroom", new Classroom());
        return "classroom-form";
    }

    @GetMapping("/classroom/edit-page/{id}")
    public String classroomEditPage(@PathVariable Integer id, Model model, Principal principal) {
        Classroom classroom = classroomService.findById(id);
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "classroom");
        model.addAttribute("classroom", classroom);
        return "classroom-form";
    }

    // ========== 课程管理页面 ==========
    @GetMapping("/course/list-page")
    public String courseList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "course");
        return "course-list";
    }

    @GetMapping("/course/add-page")
    public String courseAddPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "course");
        model.addAttribute("course", new Course());
        return "course-form";
    }

    @GetMapping("/course/edit-page/{courseId}")
    public String courseEditPage(@PathVariable String courseId, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "course");
        model.addAttribute("course", courseService.findById(courseId));
        return "course-form";
    }

    // ========== 课次管理页面 ==========
    @GetMapping("/course-session/list-page")
    public String sessionList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSession");
        return "course-session-list";
    }

    @GetMapping("/course-session/add-page")
    public String sessionAddPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSession");
        model.addAttribute("session", new CourseSession());
        return "course-session-form";
    }

    @GetMapping("/course-session/edit-page/{sessionId}")
    public String sessionEditPage(@PathVariable Long sessionId, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSession");
        model.addAttribute("session", courseSessionService.findById(sessionId));
        return "course-session-form";
    }

    // ========== 选课管理页面 ==========
    @GetMapping("/course-selection/list-page")
    public String selectionList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSelection");
        return "course-selection-list";
    }

    @GetMapping("/course-selection/add-page")
    public String selectionAddPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSelection");
        model.addAttribute("selection", new CourseSelection());
        return "course-selection-form";
    }

    @GetMapping("/course-selection/edit-page/{id}")
    public String selectionEditPage(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "courseSelection");
        model.addAttribute("selection", courseSelectionService.findById(id));
        return "course-selection-form";
    }

    // ========== 考勤管理页面 ==========
    @GetMapping("/attendance/list-page")
    public String attendanceList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "attendance");
        return "attendance-list";
    }

    @GetMapping("/attendance/statistics-page")
    public String attendanceStatistics(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", "attendance");
        return "attendance-statistics";
    }

    private String extractRole(Authentication authentication) {
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String authStr = auth.getAuthority();
            if (authStr.startsWith("ROLE_")) {
                return authStr.substring(5);
            }
            return authStr;
        }
        return "";
    }
}
