package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class PageController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/student/list-page")
    public String studentList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "student-list";
    }

    @GetMapping("/student/add-page")
    public String addPage(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @GetMapping("/student/edit-page/{studentId}")
    public String editPage(@PathVariable String studentId, Model model) {
        Student student = studentService.findById(studentId);
        model.addAttribute("student", student);
        return "student-form";
    }
}
