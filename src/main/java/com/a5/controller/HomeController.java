package com.a5.controller;

import com.a5.service.StudentService;
import com.a5.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.transaction.annotation.Transactional;

/**
 * Controller for handling requests to the home page of the application.
 * Provides an overview of key statistics and information.
 */
@Controller
public class HomeController {
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;

    /**
     * Handles requests to the application's home page.
     * Adds statistics about students and courses to the model for display.
     * 
     * @param model The Spring MVC model to populate with data
     * @return The name of the view template to render (home.html)
     */
    @GetMapping("/")
    @Transactional(readOnly = true)
    public String home(Model model) {
        var students = studentService.getAllStudents();
        var courses = courseService.getAllCourses();

        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalCourses", courses.size());
        model.addAttribute("students", students);

        model.addAttribute("courseNames", courses.stream().map(c -> c.getName()).toList());
        model.addAttribute("studentCounts", courses.stream().map(c -> c.getStudents().size()).toList());

        return "home";
    }

} 