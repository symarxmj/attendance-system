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

    @Autowired private StudentService studentService;
    @Autowired private UserService userService;
    @Autowired private ClassroomService classroomService;
    @Autowired private CourseService courseService;
    @Autowired private CourseSessionService courseSessionService;
    @Autowired private CourseSelectionService courseSelectionService;
    @Autowired private AttendanceService attendanceService;

    private void addCommon(Model model, Principal principal, Authentication auth, String page) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", page);
        model.addAttribute("role", extractRole(auth));
    }

    @GetMapping("/login")    public String login()    { return "login"; }
    @GetMapping("/register") public String register()  { return "register"; }

    @GetMapping("/welcome")
    public String welcome(Model m, Principal p, Authentication a) { addCommon(m,p,a,"welcome"); return "welcome"; }

    // ========== 学生管理 ==========
    @GetMapping("/student/list-page")
    public String studentList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"student"); return "student-list"; }
    @GetMapping("/student/add-page")
    public String addPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"student"); m.addAttribute("student",new Student()); return "student-form"; }
    @GetMapping("/student/edit-page/{studentId}")
    public String editPage(@PathVariable String studentId, Model m, Principal p, Authentication a) { addCommon(m,p,a,"student"); m.addAttribute("student",studentService.findById(studentId)); return "student-form"; }
    @GetMapping("/student/import-page")
    public String studentImport(Model m, Principal p, Authentication a) { addCommon(m,p,a,"student"); return "student-import"; }

    // ========== 用户管理 ==========
    @GetMapping("/user/list-page")
    public String userList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"user"); return "user-list"; }
    @GetMapping("/user/add-page")
    public String userAddPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"user"); m.addAttribute("user",new User()); return "user-form"; }
    @GetMapping("/user/edit-page/{id}")
    public String userEditPage(@PathVariable Long id, Model m, Principal p, Authentication a) { addCommon(m,p,a,"user"); User u=userService.findById(id); u.setPassword(null); m.addAttribute("user",u); return "user-form"; }

    // ========== 教室管理 ==========
    @GetMapping("/classroom/list-page")
    public String classroomList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"classroom"); return "classroom-list"; }
    @GetMapping("/classroom/add-page")
    public String classroomAddPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"classroom"); m.addAttribute("classroom",new Classroom()); return "classroom-form"; }
    @GetMapping("/classroom/edit-page/{id}")
    public String classroomEditPage(@PathVariable Integer id, Model m, Principal p, Authentication a) { addCommon(m,p,a,"classroom"); m.addAttribute("classroom",classroomService.findById(id)); return "classroom-form"; }

    // ========== 课程管理 ==========
    @GetMapping("/course/list-page")
    public String courseList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"course"); return "course-list"; }
    @GetMapping("/course/add-page")
    public String courseAddPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"course"); m.addAttribute("course",new Course()); return "course-form"; }
    @GetMapping("/course/edit-page/{courseId}")
    public String courseEditPage(@PathVariable String courseId, Model m, Principal p, Authentication a) { addCommon(m,p,a,"course"); m.addAttribute("course",courseService.findById(courseId)); return "course-form"; }

    // ========== 课次管理 ==========
    @GetMapping("/course-session/list-page")
    public String sessionList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSession"); return "course-session-list"; }
    @GetMapping("/course-session/add-page")
    public String sessionAddPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSession"); m.addAttribute("session",new CourseSession()); return "course-session-form"; }
    @GetMapping("/course-session/edit-page/{sessionId}")
    public String sessionEditPage(@PathVariable Long sessionId, Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSession"); m.addAttribute("session",courseSessionService.findById(sessionId)); return "course-session-form"; }

    // ========== 选课管理 ==========
    @GetMapping("/course-selection/list-page")
    public String selectionList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSelection"); return "course-selection-list"; }
    @GetMapping("/course-selection/add-page")
    public String selectionAddPage(Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSelection"); m.addAttribute("selection",new CourseSelection()); return "course-selection-form"; }
    @GetMapping("/course-selection/edit-page/{id}")
    public String selectionEditPage(@PathVariable Long id, Model m, Principal p, Authentication a) { addCommon(m,p,a,"courseSelection"); m.addAttribute("selection",courseSelectionService.findById(id)); return "course-selection-form"; }

    // ========== 考勤管理 ==========
    @GetMapping("/attendance/list-page")
    public String attendanceList(Model m, Principal p, Authentication a) { addCommon(m,p,a,"attendance"); return "attendance-list"; }
    @GetMapping("/attendance/statistics-page")
    public String attendanceStatistics(Model m, Principal p, Authentication a) { addCommon(m,p,a,"attendance"); return "attendance-statistics"; }

    private String extractRole(Authentication authentication) {
        if (authentication == null) return "";
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String authStr = auth.getAuthority();
            if (authStr.startsWith("ROLE_")) {
                return authStr.substring(5);
            }
        }
        return "";
    }
}